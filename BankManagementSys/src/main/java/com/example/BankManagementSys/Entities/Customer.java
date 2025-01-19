package com.example.BankManagementSys.Entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.Column;
import java.util.Date;

@Entity

@DiscriminatorValue("CUSTOMER")
@Data
public class Customer extends User {

    @Column(name = "joining_date")
    private Date joinDate;

}