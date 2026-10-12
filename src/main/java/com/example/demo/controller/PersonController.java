package com.example.demo.controller;

import com.example.demo.model.Person;
import com.example.demo.model.Address;
import com.example.demo.model.City;
import com.example.demo.repository.CityRepository;
import com.example.demo.repository.PersonRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/persons")
public class PersonController {
    private final PersonRepository personRepository;
    private final CityRepository cityRepository;

    public PersonController(PersonRepository personRepository, CityRepository cityRepository) {
        this.personRepository = personRepository;
        this.cityRepository = cityRepository;
    }

    @GetMapping
    public String getAllPersons(Model model) {
        model.addAttribute("persons", personRepository.findAll());
        return "persons";
    }

    @GetMapping("/new")
    public String createPersonForm(Model model) {
        Person person = new Person();
        person.setAddress(new Address());
        model.addAttribute("person", person);
        model.addAttribute("cities", cityRepository.findAll());
        return "person-form";
    }

    @GetMapping("/edit/{id}")
    public String editPersonForm(@PathVariable("id") Long id, Model model) {
        Optional<Person> personOptional = personRepository.findById(id);
        if (personOptional.isEmpty()) {
            return "redirect:/persons";
        }

        Person person = personOptional.get();
        if (person.getAddress() == null) {
            person.setAddress(new Address());
        }

        model.addAttribute("person", person);
        model.addAttribute("cities", cityRepository.findAll());
        return "person-form";
    }

    @PostMapping("/save")
    public String savePerson(@Valid @ModelAttribute("person") Person person, BindingResult result,
                             @RequestParam("address.city.id") Long cityId, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("cities", cityRepository.findAll());
            return "person-form";
        }

        City city = cityRepository.findById(cityId).orElse(null);
        if (city != null) {
            person.getAddress().setCity(city);
        }

        personRepository.save(person);
        return "redirect:/persons";
    }

    @GetMapping("/delete/{id}")
    public String deletePerson(@PathVariable("id") Long id) {
        if (personRepository.existsById(id)) {
            personRepository.deleteById(id);
        }
        return "redirect:/persons";
    }

    @PostMapping("/search")
    public String searchPersons(@RequestParam("criteria") String criteria,
                                @RequestParam("keyword") String keyword, Model model) {
        if (keyword == null || keyword.isBlank()) {
            return "redirect:/persons";
        }

        switch (criteria) {
            case "lastName":
                model.addAttribute("persons", personRepository.findByLastNameContainingIgnoreCase(keyword));
                break;
            case "city":
                model.addAttribute("persons", personRepository.findByAddressCityNameContainingIgnoreCase(keyword));
                break;
            case "gender":
                model.addAttribute("persons", personRepository.findByGenderIgnoreCase(keyword));
                break;
            default:
                model.addAttribute("persons", personRepository.findAll());
        }

        return "persons";
    }
}