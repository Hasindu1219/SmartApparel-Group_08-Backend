package com.example.SmartApparel.Operations.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entity class representing the salary details of an employee.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Salary {

    // Unique identifier for the salary record
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int salaryId;

    // Identifier for the employee to whom the salary record belongs
    @Column(nullable = false)
    private String empId;

    // Status of the salary (e.g., paid, pending)
    @Column(nullable = false)
    private String status;

    // Year and month for which the salary is applicable, in the format YYYY-MM
    @Column(nullable = false)
    private String yearNMonth;

    // Basic salary for the month
    @Column(precision = 8,scale = 2,nullable = false)
    private BigDecimal basic;

    // OT for the month
    @Column(precision = 8,scale = 2,nullable = false)
    private BigDecimal overTime;

    // Employee's contribution to the Employees' Provident Fund (EPF)
    @Column(precision = 8,scale = 2,nullable = false)
    private BigDecimal epfByEmployee;

    // Company's contribution to the Employees' Provident Fund (EPF)
    @Column(precision = 8,scale = 2,nullable = false)
    private BigDecimal epfByCompany;

    // Employees' Trust Fund (ETF) payment
    @Column(precision = 8,scale = 2,nullable = false)
    private BigDecimal etfPayment;

    // Allowance1 for the month
    @Column(precision = 8,scale = 2,nullable = false)
    private BigDecimal allowance1;

    // Allowance2 for the month
    @Column(precision = 8,scale = 2,nullable = false)
    private BigDecimal allowance2;

    // Net salary after all deductions
    @Column(precision = 8,scale = 2,nullable = false)
    private BigDecimal netSalary;
}
