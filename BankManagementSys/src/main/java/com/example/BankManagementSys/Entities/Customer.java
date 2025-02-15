package com.example.BankManagementSys.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "customers")
@Data
public class Customer extends User {
    @Column(name = "joining_date")
    private Date joinDate;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonManagedReference(value = "bankAccount-customer") // ✅ Required to match BankAccount
    private List<BankAccount> bankAccounts;


}
