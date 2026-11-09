package com.example.demo.repository;

import com.example.demo.model.City;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CityRepository {

    private final JdbcTemplate jdbcTemplate;

    public CityRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<City> cityRowMapper = (rs, rowNum) -> {
        City city = new City();
        city.setId(rs.getLong("id"));
        city.setName(rs.getString("name"));
        city.setPostalCode(rs.getString("postal_code"));
        return city;
    };

    public List<City> findAll() {
        String sql = "SELECT id, name, postal_code FROM cities ORDER BY id";
        return jdbcTemplate.query(sql, cityRowMapper);
    }

    public Optional<City> findById(Long id) {
        String sql = "SELECT id, name, postal_code FROM cities WHERE id = ?";
        List<City> result = jdbcTemplate.query(sql, cityRowMapper, id);
        return result.stream().findFirst();
    }
}