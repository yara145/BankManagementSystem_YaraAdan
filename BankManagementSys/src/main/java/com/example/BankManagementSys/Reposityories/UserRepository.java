package com.example.BankManagementSys.Reposityories;

import com.example.BankManagementSys.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {
}
