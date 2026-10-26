package com.example.demo.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class PersonRequestDTO {

    @NotBlank(message = "Фамилия обязательна")
    private String lastName;

    @NotBlank(message = "Имя обязательно")
    private String firstName;

    private String middleName;
    private String gender;
    private String nationality;

    @Positive(message = "Рост должен быть положительным")
    private Double height;

    @Positive(message = "Вес должен быть положительным")
    private Double weight;

    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthDate;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Некорректный телефон")
    private String phoneNumber;

    @NotNull(message = "ID города обязателен")
    private Long cityId;

    @NotBlank(message = "Улица обязательна")
    private String street;

    @NotBlank(message = "Дом обязателен")
    private String house;

    private String apartment;
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public Long getCityId() { return cityId; }
    public void setCityId(Long cityId) { this.cityId = cityId; }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getHouse() { return house; }
    public void setHouse(String house) { this.house = house; }
    public String getApartment() { return apartment; }
    public void setApartment(String apartment) { this.apartment = apartment; }
}