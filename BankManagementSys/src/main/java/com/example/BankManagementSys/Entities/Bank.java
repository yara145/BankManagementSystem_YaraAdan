package com.example.BankManagementSys.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="banks")
@Data
public class Bank implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name", nullable = false)
    private String name;
    @ToString.Exclude
    @OneToMany(mappedBy = "bank")
    private List<Branch> branches;

}
