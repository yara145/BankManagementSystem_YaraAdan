package com.example.BankManagementSys.Entities;

import com.example.BankManagementSys.Enums.BankAccountStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

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

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @PrePersist
    public void onPrePersist() {
        this.createdDate = LocalDateTime.now();
    }

    @Column(name = "account_type", nullable = false)
    private String type;

    @Column(name = "balance")
    private BigDecimal balance = BigDecimal.ZERO;


    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BankAccountStatus status;

    @JoinColumn(name = "branch_id")
    @ToString.Exclude   // exclude branch from bank account
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Branch branch;

    @JoinColumn(name = "customer_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonBackReference
    private Customer customer;

    @ManyToMany(mappedBy = "bankAccounts")
    @JsonIgnore
    @ToString.Exclude
    private List<Employee> employees;

    @OneToMany(mappedBy = "bankAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnore
    private List<Transaction> transactions;

    @Column(name = "currency_code", nullable = false)
    private String currencyCode = "ILS"; // Default currency is Shekel


}
