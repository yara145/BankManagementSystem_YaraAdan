package com.example.BankManagementSys.Entities;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;
@Entity
@Table(name = "loan_payments")
@Data
public class LoanPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentNumber;


    @Column(name = "payment_amount")
    private double paymentAmount;

    @Column(name = "payment_date_time", nullable = false, updatable = false)
    private LocalDateTime paymentDateTime;


    @JoinColumn(name = "loan_id", nullable = false)
    @ManyToOne
    @ToString.Exclude
    private Loan loan;


}
