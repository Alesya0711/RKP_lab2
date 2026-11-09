package com.example.demo.repository;

import com.example.demo.model.Address;
import com.example.demo.model.City;
import com.example.demo.model.Person;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonMapper implements RowMapper<Person> {
    @Override
    public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
        // город
        City city = new City();
        city.setId(rs.getLong("city_id"));
        city.setName(rs.getString("city_name"));
        city.setPostalCode(rs.getString("postal_code"));

        // адрес
        Address address = new Address();
        address.setId(rs.getLong("address_id"));
        address.setStreet(rs.getString("street"));
        address.setHouse(rs.getString("house"));
        address.setApartment(rs.getString("apartment"));
        address.setCity(city);

        // человек
        Person person = new Person();
        person.setId(rs.getLong("person_id"));
        person.setLastName(rs.getString("last_name"));
        person.setFirstName(rs.getString("first_name"));
        person.setMiddleName(rs.getString("middle_name"));
        person.setGender(rs.getString("gender"));
        person.setNationality(rs.getString("nationality"));
        person.setHeight(rs.getDouble("height"));
        person.setWeight(rs.getDouble("weight"));
        person.setPhoneNumber(rs.getString("phone_number"));

        if (rs.getDate("birth_date") != null) {
            person.setBirthDate(rs.getDate("birth_date").toLocalDate());
        }

        person.setAddress(address);

        return person;
    }
}