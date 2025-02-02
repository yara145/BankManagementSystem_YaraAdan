package com.example.BankManagementSys.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Bank bank;

    @ToString.Exclude
    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<BankAccount> bankAccounts;

    @ToString.Exclude
    @ManyToMany(mappedBy = "branches")
    @JsonIgnore
    private List<Employee> employees;
}
