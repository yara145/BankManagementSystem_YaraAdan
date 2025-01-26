package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.User;
import com.example.BankManagementSys.Reposityories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void validateUser(User user) {
        try {
            if (!user.getIdNumber().matches("\\d{9}")) {
                throw new IllegalArgumentException("ID number must be exactly 9 digits.");
            }
            if (!userRepository.findUsersByUserNameEquals(user.getUserName()).isEmpty()) {
                throw new IllegalArgumentException("Username already exists.");
            }
            if (!userRepository.findUsersByIdNumberEquals(user.getIdNumber()).isEmpty()) {
                throw new IllegalArgumentException("ID number already exists.");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Validation failed: " + e.getMessage());
        }
    }

    public int calculateAge(Date birthdate) {
        return userRepository.calculateAge(birthdate);
    }

    public List<User> findUsersByProvider(String provider) {
        return userRepository.getUsersWithSpecificProvider(provider);
    }

    public List<User> findUsersByIdNumberPattern(String pattern) {
        return userRepository.getUsersWithSpecificIdProvider(pattern);
    }

}
