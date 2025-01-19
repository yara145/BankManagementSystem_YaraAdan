package com.example.BankManagementSys.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
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
    private String descriptipn;



}



