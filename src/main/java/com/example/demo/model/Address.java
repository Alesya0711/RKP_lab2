package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Улица обязательна")
    @Column(name = "street", nullable = false, length = 150)
    private String street;

    @NotBlank(message = "Номер дома обязателен")
    @Column(name = "house", nullable = false, length = 20)
    private String house;

    @Column(name = "apartment", length = 20)
    private String apartment;

    @NotNull(message = "Город должен быть выбран")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    public Address() {}

    public Address(String street, String house, String apartment, City city) {
        this.street = street;
        this.house = house;
        this.apartment = apartment;
        this.city = city;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getHouse() { return house; }
    public void setHouse(String house) { this.house = house; }

    public String getApartment() { return apartment; }
    public void setApartment(String apartment) { this.apartment = apartment; }

    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }
}