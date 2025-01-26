package com.example.BankManagementSys.Entities;
import com.example.BankManagementSys.Enums.TransferStatus;
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

    @Column(name = "transfer_name")
    private String transferName;

    @Column(name = "transfer_account_num")
    private int transferAccountNum;

    @Column(name = "amount")
    private double amount;

    @Column(name = "bank_code")
    private int transferBankCode;

    @Column(name = "branch_code")
    private int transferBranchCode;

    @Column(name = "transfer_date")
    private Date transferDate;

    @Enumerated(EnumType.STRING)  // This stores the enum as a string in the database
    @Column(name = "transfer_status", nullable = false)
    private TransferStatus transferStatus;


}
