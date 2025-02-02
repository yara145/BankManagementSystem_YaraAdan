package com.example.BankManagementSys.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="Withdrawal")
@Data
@DiscriminatorValue("Withdrawal")

public class WithdrawalTransaction extends Transaction{

    @Column(name = "withdrawal_amount")
    private BigDecimal withdrawalAmount;


}
