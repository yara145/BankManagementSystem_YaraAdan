package com.example.BankManagementSys.Entities;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import com.example.BankManagementSys.Enums.LoanType;
@Entity
@Table(name="loans")
@Data
@DiscriminatorValue("Loan")

public class Loan extends Transaction  {

    @Column(name = "loan_name")
    private String loanName;

    @Column(name = "start_payment_date")
    private Date startPaymentDate;

    @Column(name = "number_of_payments", nullable = false)
    private int numberOfPayments = 0;

    @Column(name = "remaining_payments_num", nullable = false)
    private int remainingPaymentsNum = 0;

    @Column(name = "end_payment_date")
    private Date endPaymentDate;

    @Column(name = "loan_amount")
    private BigDecimal loanAmount;

    @Column(name = "interest_rate")
    private Double interestRate;

    @Column(name = "remaining_balance")
    private double remainingBalance;

    @Column(name = "loan_date")
    private Date loanDate;

    @Column(name = "loan_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LoanType loanType;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<LoanPayment> payments = new ArrayList<>();  // ✅ Initialize list



}