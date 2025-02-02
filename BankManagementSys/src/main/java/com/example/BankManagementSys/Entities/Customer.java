package com.example.BankManagementSys.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore // ✅ Prevents bank accounts from appearing in customer responses
    private List<BankAccount> bankAccounts;
}
