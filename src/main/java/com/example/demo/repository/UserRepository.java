package com.example.demo.repository;

import com.example.demo.model.AppUser;
import com.example.demo.model.Role;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<AppUser> userMapper = (rs, rowNum) -> {
        AppUser user = new AppUser();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(Role.valueOf(rs.getString("role")));
        return user;
    };

    public Optional<AppUser> findByUsername(String username) {
        return jdbcTemplate.query(
                "SELECT id, username, password, role FROM users WHERE username = ?",
                userMapper, username).stream().findFirst();
    }
}