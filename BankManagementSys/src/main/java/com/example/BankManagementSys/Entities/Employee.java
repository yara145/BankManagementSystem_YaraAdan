package com.example.BankManagementSys.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "employees")
@Data
public class Employee extends User {

    @Column(name = "work_start_date")
    private Date startDate;

    @ManyToMany
    @JoinTable(
            name = "employee_bankaccounts",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "bankaccount_id")
    )
    @JsonIgnore
    private List<BankAccount> bankAccounts;

    @ManyToMany
    @JoinTable(
            name = "employees_branches",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "branch_id")
    )
    @JsonIgnore
    private List<Branch> branches;
}
