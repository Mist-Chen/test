package com.sztus.teldrassil.sprint.api.view;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class EmployeePersonalView {
    private Long employeeId;

    @NotNull(message = "first name is required")
    private String firstName;

    private String middleName;

    @NotBlank(message = "last name is required")
    private String lastName;

    private Long birthday;

    @NotBlank(message = "email is required")
    private String email;

    private String telephone;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
