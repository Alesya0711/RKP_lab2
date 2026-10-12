package com.example.demo.repository;

import com.example.demo.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    // Поиск по фамилии
    List<Person> findByLastNameContainingIgnoreCase(String lastName);

    // Поиск по названию города в адресе
    List<Person> findByAddressCityNameContainingIgnoreCase(String cityName);

    // Поиск по полу
    List<Person> findByGenderIgnoreCase(String gender);
}