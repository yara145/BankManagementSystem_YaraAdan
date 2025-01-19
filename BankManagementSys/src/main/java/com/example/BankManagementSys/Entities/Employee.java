package com.example.BankManagementSys.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "employees")  // Table name for Employee entity
@Data
public class Employee extends User {

    @Column(name = "work_start_date")
    private Date startDate;

    @ManyToMany
    @JoinTable(
            name = "employee_bankaccounts", // Join table name
            joinColumns = @JoinColumn(name = "employee_id"), // Column for this entity
            inverseJoinColumns = @JoinColumn(name = "bankaccount_id") // Column for the other entity
    )
    private List<BankAccount> bankAccounts;

    @ManyToMany
    @JoinTable(
            name = "employees_branches", // Join table name
            joinColumns = @JoinColumn(name = "employee_id"), // Column for this entity
            inverseJoinColumns = @JoinColumn(name = "branch_id") // Column for the other entity
    )
    private List<Branch> branches;

}
