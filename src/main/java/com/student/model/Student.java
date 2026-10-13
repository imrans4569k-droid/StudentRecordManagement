package com.student.model;

/**
 * Student Model / Entity Class.
 * Represents a student record in the MySQL database.
 */
public class Student {

    private Long id;
    private String name;
    private String roll;
    private String department;
    private String year;
    private String email;
    private String phone;

    // Default constructor (required by serialization frameworks)
    public Student() {
    }

    // Parameterized constructor
    public Student(Long id, String name, String roll, String department, String year, String email, String phone) {
        this.id = id;
        this.name = name;
        this.roll = roll;
        this.department = department;
        this.year = year;
        this.email = email;
        this.phone = phone;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", roll='" + roll + '\'' +
                ", department='" + department + '\'' +
                ", year='" + year + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
