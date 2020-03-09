package com.sonde.sample.model;

public class SignUpRequest {
    private Integer yearOfBirth;
    private String gender;
    private String language;


    public SignUpRequest(Integer yearOfBirth, String gender, String language) {
        this.yearOfBirth = yearOfBirth;
        this.gender = gender;
        this.language = language;
    }

    public Integer getYearOfBirth() {
        return yearOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getLanguage() {
        return language;
    }
}
