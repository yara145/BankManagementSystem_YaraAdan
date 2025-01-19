package com.example.BankManagementSys.Entities;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="loans")
@Data
@DiscriminatorValue("Loan")

public class Loan extends Transaction  {

    @Column(name = "loan_id")
    private String loanId;

    @Column(name = "start_payment_date")
    private Date startPaymentDate;

    @Column(name = "number_of_payments")
    private int numberOfPayments;

    @Column(name = "end_payment_date")
    private Date endPaymentDate;

    @Column(name = "loan_amount")
    private double loanAmount;

    @Column(name = "interest_rate")
    private double interestRate;

    @Column(name = "remaining_balance")
    private double remainingBalance;

    @Column(name = "loan_date")
    private Date loanDate;


    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanPayment> payments = new ArrayList<>(); // Changed to List

}