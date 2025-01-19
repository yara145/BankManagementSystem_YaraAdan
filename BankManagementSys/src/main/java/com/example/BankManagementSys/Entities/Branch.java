package com.example.BankManagementSys.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="branch")
@Data
public class Branch implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="branch_name", nullable = false)
    private String name;

    @Column(name="location")
    private String location;

    @JoinColumn(name = "bank_id")
    @ManyToOne()
    private Bank bank;

    @OneToMany(mappedBy = "branch")
    private List<BankAccount> bankAccounts;

    @ManyToMany(mappedBy = "branches")
    private List<Employee> employees;

}
