package com.example.BankManagementSys.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

 // Table-per-class inheritance strategy
@Data
@MappedSuperclass
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)  // Use AUTO for ID generation (or you can use UUID)
    private String idCode;  // Unique ID (can be UUID or string format)

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "idNumber", nullable = false)
    private int idNumber;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "birthdate", nullable = false)
    private Date birthdate;

    @PrePersist
    public void onPrePersist() {
        if (this.idCode == null) {
            this.idCode = UUID.randomUUID().toString();  // Generate UUID if idCode is not set
        }
    }
}
