package com.example.BankManagementSys.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.Column;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "customers") // Table for Customer entity
@Data
public class Customer extends User {

    @Column(name = "joining_date")
    private Date joinDate;
    @ToString.Exclude
    @OneToMany(mappedBy = "customer")
    private List<BankAccount> bankAccounts;
}

