package com.example.demo.repository;

import com.example.demo.model.Person;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class PersonRepository {

    private final JdbcTemplate jdbcTemplate;

    public PersonRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final String SELECT_ALL_SQL = """
            SELECT p.id as person_id, p.last_name, p.first_name, p.middle_name, 
                   p.gender, p.nationality, p.height, p.weight, p.birth_date, p.phone_number,
                   a.id as address_id, a.street, a.house, a.apartment,
                   c.id as city_id, c.name as city_name, c.postal_code
            FROM persons p
            LEFT JOIN addresses a ON p.address_id = a.id
            LEFT JOIN cities c ON a.city_id = c.id
            """;

    public List<Person> findAll() {
        return jdbcTemplate.query(SELECT_ALL_SQL, new PersonMapper());
    }

    public Optional<Person> findById(Long id) {
        String sql = SELECT_ALL_SQL + " WHERE p.id = ?";
        List<Person> result = jdbcTemplate.query(sql, new PersonMapper(), id);
        return result.stream().findFirst();
    }

    public boolean existsById(Long id) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM persons WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM persons WHERE id = ?", id);
    }

    @Transactional
    public Person save(Person person) {
        if (person.getId() == null) {
            KeyHolder addressKeyHolder = new GeneratedKeyHolder();
            String addressSql = "INSERT INTO addresses (street, house, apartment, city_id) VALUES (?, ?, ?, ?)";

            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(addressSql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, person.getAddress().getStreet());
                ps.setString(2, person.getAddress().getHouse());
                ps.setString(3, person.getAddress().getApartment());
                ps.setLong(4, person.getAddress().getCity().getId());
                return ps;
            }, addressKeyHolder);

            Long addressId = addressKeyHolder.getKey().longValue();
            person.getAddress().setId(addressId);

            KeyHolder personKeyHolder = new GeneratedKeyHolder();
            String personSql = "INSERT INTO persons (last_name, first_name, middle_name, gender, nationality, height, weight, birth_date, phone_number, address_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(personSql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, person.getLastName());
                ps.setString(2, person.getFirstName());
                ps.setString(3, person.getMiddleName());
                ps.setString(4, person.getGender());
                ps.setString(5, person.getNationality());

                if (person.getHeight() != null) ps.setDouble(6, person.getHeight()); else ps.setNull(6, Types.DOUBLE);
                if (person.getWeight() != null) ps.setDouble(7, person.getWeight()); else ps.setNull(7, Types.DOUBLE);
                if (person.getBirthDate() != null) ps.setDate(8, Date.valueOf(person.getBirthDate())); else ps.setNull(8, Types.DATE);

                ps.setString(9, person.getPhoneNumber());
                ps.setLong(10, addressId);
                return ps;
            }, personKeyHolder);

            person.setId(personKeyHolder.getKey().longValue());
        } else {
            String addressSql = "UPDATE addresses SET street = ?, house = ?, apartment = ?, city_id = ? WHERE id = ?";
            jdbcTemplate.update(addressSql,
                    person.getAddress().getStreet(), person.getAddress().getHouse(),
                    person.getAddress().getApartment(), person.getAddress().getCity().getId(),
                    person.getAddress().getId());

            String personSql = "UPDATE persons SET last_name = ?, first_name = ?, middle_name = ?, gender = ?, nationality = ?, height = ?, weight = ?, birth_date = ?, phone_number = ? WHERE id = ?";
            jdbcTemplate.update(personSql,
                    person.getLastName(), person.getFirstName(), person.getMiddleName(), person.getGender(),
                    person.getNationality(), person.getHeight(), person.getWeight(),
                    person.getBirthDate(), person.getPhoneNumber(), person.getId());
        }
        return person;
    }

    // Поиск по фамилии
    public List<Person> findByLastNameContainingIgnoreCase(String keyword) {
        String sql = SELECT_ALL_SQL + " WHERE LOWER(p.last_name) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, new PersonMapper(), "%" + keyword + "%");
    }

    // Поиск по названию города
    public List<Person> findByAddressCityNameContainingIgnoreCase(String keyword) {
        String sql = SELECT_ALL_SQL + " WHERE LOWER(c.name) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, new PersonMapper(), "%" + keyword + "%");
    }

    // Поиск по полу
    public List<Person> findByGenderIgnoreCase(String keyword) {
        String sql = SELECT_ALL_SQL + " WHERE LOWER(p.gender) = LOWER(?)";
        return jdbcTemplate.query(sql, new PersonMapper(), keyword);
    }
}