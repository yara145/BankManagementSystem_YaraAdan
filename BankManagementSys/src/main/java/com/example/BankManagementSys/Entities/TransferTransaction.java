package com.example.BankManagementSys.Entities;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="transfers")
@Data
@DiscriminatorValue("TRANSFER")

public class TransferTransaction extends Transaction  {

    @Column(name = "name")
    private String name;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "amount")
    private double amount;

    @Column(name = "bank_code")
    private int bankCode;

    @Column(name = "branch_code")
    private int branchCode;

    @Column(name = "transfer_date")
    private Date transferDate;
}
