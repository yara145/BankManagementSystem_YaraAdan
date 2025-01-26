package com.example.BankManagementSys.Entities;

import com.example.BankManagementSys.Enums.BankAccountStatus;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="bank_accounts")
@Data
@Getter
public class BankAccount implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Adding the date of creation
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    // Constructor to initialize the creation date
    @PrePersist
    public void onPrePersist() {
        this.createdDate = LocalDateTime.now(); // set the current date
    }
    @Column(name = "account_type", nullable = false)
    private String type;

    @Column(name = "balance")//it can be null at first
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)  // This stores the enum as a string in the database
    @Column(name = "status")
    private BankAccountStatus status;

    @JoinColumn(name = "branch_id")
    @ManyToOne()
    private Branch branch;

    @JoinColumn(name = "customer_id")
    @ManyToOne()
    private Customer customer;

    @ManyToMany(mappedBy = "bankAccounts")
    private List<Employee> employees;


    @OneToMany(mappedBy = "bankAccount")
    private List<Transaction> transactions;

}
