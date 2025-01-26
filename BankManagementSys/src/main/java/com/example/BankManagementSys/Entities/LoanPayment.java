package com.example.BankManagementSys.Entities;
import jakarta.persistence.*;
import lombok.Data;

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

    @Column(name = "payment_date")
    private Date paymentDate;

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;


}
