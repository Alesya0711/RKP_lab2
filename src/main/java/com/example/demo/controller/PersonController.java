package com.example.demo.controller;

import com.example.demo.model.Person;
import com.example.demo.repository.PersonRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/persons")
public class PersonController {

    private final PersonRepository personRepository;

    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    // Список всех записей
    @GetMapping
    public String getAllPersons(Model model) {
        model.addAttribute("persons", personRepository.findAll());
        return "persons";
    }

    // Просмотр детальной информации по ID
    @GetMapping("/details/{id}")
    public String getPersonDetails(@PathVariable("id") Long id, Model model) {
        Optional<Person> personOptional = personRepository.findById(id);
        if (personOptional.isEmpty()) {
            return "redirect:/persons";
        }
        model.addAttribute("person", personOptional.get());
        return "person-details";
    }

    // Форма добавления нового человека
    @GetMapping("/new")
    public String createPersonForm(Model model) {
        model.addAttribute("person", new Person());
        return "person-form";
    }

    // Сохранение (нового или отредактированного)
    @PostMapping("/save")
    public String savePerson(@ModelAttribute("person") Person person) {
        personRepository.save(person);
        return "redirect:/persons";
    }

    // Форма редактирования
    @GetMapping("/edit/{id}")
    public String editPersonForm(@PathVariable("id") Long id, Model model) {
        Optional<Person> personOptional = personRepository.findById(id);
        if (personOptional.isEmpty()) {
            return "redirect:/persons";
        }
        model.addAttribute("person", personOptional.get());
        return "person-form";
    }

    // Удаление записи по ID
    @GetMapping("/delete/{id}")
    public String deletePerson(@PathVariable("id") Long id) {
        if (personRepository.existsById(id)) {
            personRepository.deleteById(id);
        }
        return "redirect:/persons";
    }
}
