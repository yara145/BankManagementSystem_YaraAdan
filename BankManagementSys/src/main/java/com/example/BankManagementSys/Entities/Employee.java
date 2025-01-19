

package com.example.BankManagementSys.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.Column;
import java.util.Date;

@Entity

@DiscriminatorValue("EMPLOYEE")
@Data
public class Employee extends User {

    @Column(name = "work_start date")
    private Date startDate;


}