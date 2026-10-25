package com.example.javaassessment.request;

import jakarta.validation.constraints.*;


import java.util.UUID;

public class CustomerRequest {
    private UUID id;
    private String firstName;
    private String lastName;
    @Email(message = "Please provide a valid personal email address")
    private String personalEmail;
    @Email(message = "Please provide a valid business email address")
    private String businessEmail;
    @Pattern(regexp = "^\\\\+?[1-9]\\\\d{1,14}$")
    private String contactNo;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }
}
