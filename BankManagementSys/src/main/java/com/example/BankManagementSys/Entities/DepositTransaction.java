package com.example.BankManagementSys.Entities;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="Desposits")
@Data
@DiscriminatorValue("Desposit")
public class DepositTransaction extends Transaction{

    @Column(name = "desposit_amount")
    private BigDecimal despositAmount;
}
