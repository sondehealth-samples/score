package com.sonde.sample.model;

public class SignUpRequest {
    private String yearOfBirth;
    private String gender;
    private String language;


    public SignUpRequest(String yearOfBirth, String gender, String language) {
        this.yearOfBirth = yearOfBirth;
        this.gender = gender;
        this.language = language;
    }

    public String getYearOfBirth() {
        return yearOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getLanguage() {
        return language;
    }
}
