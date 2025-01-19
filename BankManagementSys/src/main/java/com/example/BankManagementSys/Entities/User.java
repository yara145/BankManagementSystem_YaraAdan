package com.example.BankManagementSys.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING) // To
@MappedSuperclass
@Data
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // should be edited to unique string
    private int idCode;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "idNumber")
    private int idNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "address")
    private String address;

    @Column(name = "birthdate")
    private Date birthdate;


}
