package com.example.lab14.model;

public class Employee {
    public final int employeeId;
    public final String fullName;
    public final String department;

    public Employee(int employeeId, String fullName, String department) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.department = department;
    }
}
