package com.example.BankManagementSys.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="transactions")
@Data
@DiscriminatorColumn(name = "transaction_type", discriminatorType = DiscriminatorType.STRING) // To
@Inheritance(strategy = InheritanceType.JOINED)

public class Transaction implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transactionId;

    @Column(name = "description")
    private String description;

    @Column(name = "transaction_date_time", nullable = false, updatable = false)
    private LocalDateTime transactionDateTime;

    @JoinColumn(name = "bankaccount_id")

    @ManyToOne()
    @JsonIgnore
    private BankAccount bankAccount;



    @Column(name = "currency_code", nullable = false) // USD, EUR, etc.
    private String currencyCode = "ILS"; // Default currency is ILS

    @Column(name = "exchange_rate") // Exchange rate at transaction time
    private BigDecimal exchangeRate;


}



