package com.example.demo.controller;

import com.example.demo.dto.PersonRequestDTO;
import com.example.demo.dto.PersonResponseDTO;
import com.example.demo.model.Address;
import com.example.demo.model.City;
import com.example.demo.model.Person;
import com.example.demo.repository.CityRepository;
import com.example.demo.repository.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/persons")
public class PersonRestController {

    private final PersonRepository personRepository;
    private final CityRepository cityRepository;

    public PersonRestController(PersonRepository personRepository, CityRepository cityRepository) {
        this.personRepository = personRepository;
        this.cityRepository = cityRepository;
    }

    // Получить всех
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PersonResponseDTO> getAllPersons() {
        return personRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Получить по ID
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PersonResponseDTO getPersonById(@PathVariable("id") Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Человек с ID " + id + " не найден"));
        return mapToResponseDTO(person);
    }

    // Создать (POST)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonResponseDTO createPerson(@Valid @RequestBody PersonRequestDTO dto) {
        City city = cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new EntityNotFoundException("Город не найден"));

        Person person = new Person();
        updatePersonFromDto(person, dto, city);

        Person savedPerson = personRepository.save(person);
        return mapToResponseDTO(savedPerson);
    }

    // Обновить (PUT)
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PersonResponseDTO updatePerson(@PathVariable("id") Long id, @Valid @RequestBody PersonRequestDTO dto) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Человек не найден"));

        City city = cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new EntityNotFoundException("Город не найден"));

        updatePersonFromDto(person, dto, city);

        Person updatedPerson = personRepository.save(person);
        return mapToResponseDTO(updatedPerson);
    }

    // Удалить (DELETE)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePerson(@PathVariable("id") Long id) {
        if (!personRepository.existsById(id)) {
            throw new EntityNotFoundException("Человек не найден");
        }
        personRepository.deleteById(id);
    }

    // Статистика
    @GetMapping("/statistics")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getStatistics() {
        List<Person> all = personRepository.findAll();
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPersons", all.size());

        double avgHeight = all.stream().mapToDouble(Person::getHeight).average().orElse(0.0);
        stats.put("averageHeight", Math.round(avgHeight * 10.0) / 10.0);

        long maleCount = all.stream().filter(p -> "Мужской".equalsIgnoreCase(p.getGender())).count();
        stats.put("maleCount", maleCount);
        stats.put("femaleCount", all.size() - maleCount);

        return stats;
    }

    // Частичное обновление: переезд
    @PatchMapping("/{id}/relocate")
    @ResponseStatus(HttpStatus.OK)
    public PersonResponseDTO relocatePerson(@PathVariable Long id, @RequestParam Long newCityId, @RequestParam String newStreet) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Человек не найден"));
        City newCity = cityRepository.findById(newCityId)
                .orElseThrow(() -> new EntityNotFoundException("Новый город не найден"));

        person.getAddress().setCity(newCity);
        person.getAddress().setStreet(newStreet);

        return mapToResponseDTO(personRepository.save(person));
    }

    private void updatePersonFromDto(Person person, PersonRequestDTO dto, City city) {
        person.setLastName(dto.getLastName());
        person.setFirstName(dto.getFirstName());
        person.setMiddleName(dto.getMiddleName());
        person.setGender(dto.getGender());
        person.setNationality(dto.getNationality());
        person.setHeight(dto.getHeight());
        person.setWeight(dto.getWeight());
        person.setBirthDate(dto.getBirthDate());
        person.setPhoneNumber(dto.getPhoneNumber());

        Address address = person.getAddress() == null ? new Address() : person.getAddress();
        address.setCity(city);
        address.setStreet(dto.getStreet());
        address.setHouse(dto.getHouse());
        address.setApartment(dto.getApartment());
        person.setAddress(address);
    }

    private PersonResponseDTO mapToResponseDTO(Person person) {
        PersonResponseDTO dto = new PersonResponseDTO();
        dto.setId(person.getId());
        dto.setFullName(person.getLastName() + " " + person.getFirstName() + " " + (person.getMiddleName() != null ? person.getMiddleName() : ""));
        dto.setGender(person.getGender());
        dto.setHeight(person.getHeight());
        dto.setWeight(person.getWeight());
        dto.setBirthDate(person.getBirthDate());
        dto.setPhoneNumber(person.getPhoneNumber());

        if (person.getAddress() != null && person.getAddress().getCity() != null) {
            String addressStr = person.getAddress().getCity().getName() + ", ул. " + person.getAddress().getStreet() + ", д. " + person.getAddress().getHouse();
            if (person.getAddress().getApartment() != null && !person.getAddress().getApartment().isEmpty()) {
                addressStr += ", кв. " + person.getAddress().getApartment();
            }
            dto.setFullAddress(addressStr);
        }
        return dto;
    }
}