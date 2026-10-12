package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "cities")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название города не может быть пустым")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Почтовый индекс обязателен")
    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    public City() {}

    public City(String name, String postalCode) {
        this.name = name;
        this.postalCode = postalCode;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
}