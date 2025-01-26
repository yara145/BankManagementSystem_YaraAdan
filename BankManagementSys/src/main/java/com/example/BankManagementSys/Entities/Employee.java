package com.example.BankManagementSys.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "employees") // Table for Employee entity
@Data
public class Employee extends User {

    @Column(name = "work_start_date")
    private Date startDate;

    @ManyToMany
    @JoinTable(
            name = "employee_bankaccounts", // Join table
            joinColumns = @JoinColumn(name = "employee_id"), // Employee ID
            inverseJoinColumns = @JoinColumn(name = "bankaccount_id") // Bank account ID
    )
    private List<BankAccount> bankAccounts;

    @ManyToMany
    @JoinTable(
            name = "employees_branches", // Join table
            joinColumns = @JoinColumn(name = "employee_id"), // Employee ID
            inverseJoinColumns = @JoinColumn(name = "branch_id") // Branch ID
    )
    private List<Branch> branches;
}
