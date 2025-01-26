package com.example.BankManagementSys.Reposityories;

import com.example.BankManagementSys.Entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Calculate the age of a customer by their ID
    @Query("SELECT TIMESTAMPDIFF(YEAR, :birthdate, CURRENT_DATE)")
    Integer calculateAge(@Param("birthdate") Date birthdate);

}
