package com.example.BankManagementSys.Entities;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="loans")
@Data
@DiscriminatorValue("Loan")

public class Loan extends Transaction  {

    @Column(name = "loan_name")
    private String loanName;

    @Column(name = "start_payment_date")
    private Date startPaymentDate;

    @Column(name = "number_of_payments")
    private int numberOfPayments;

    @Column(name = "end_payment_date")
    private Date endPaymentDate;

    @Column(name = "loan_amount")
    private BigDecimal loanAmount;

    @Column(name = "interest_rate")
    private double interestRate;

    @Column(name = "remaining_balance")
    private double remainingBalance;

    @Column(name = "loan_date")
    private Date loanDate;


    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<LoanPayment> payments ;// Changed to List

}