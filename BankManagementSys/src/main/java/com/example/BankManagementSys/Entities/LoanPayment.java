package com.example.BankManagementSys.Entities;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
@Entity
@Table(name = "loan_payments")
@Data
public class LoanPayment {

    @Id
    private int paymentNumber;

    @Column(name = "payment_amount")
    private double paymentAmount;

    @Column(name = "payment_datw")
    private Date paymentDate;

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;


}
