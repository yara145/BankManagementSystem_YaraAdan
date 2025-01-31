package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.User;
import com.example.BankManagementSys.Reposityories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    // Email validation regex pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    // Name validation regex pattern (Only letters, spaces allowed)
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]+(?: [A-Za-z]+)*$");


    public void validateUser(User user) {
        try {
            if (!user.getIdNumber().matches("\\d{9}")) {
                throw new IllegalArgumentException("ID number must be exactly 9 digits.");
            }
            // Validate Email Format
            if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
                throw new IllegalArgumentException("Invalid email format.");
            }

            // Validate First & Last Name (Only letters)
            if (!NAME_PATTERN.matcher(user.getFirstName()).matches()) {
                throw new IllegalArgumentException("First name must contain only letters.");
            }

            if (!NAME_PATTERN.matcher(user.getLastName()).matches()) {
                throw new IllegalArgumentException("Last name must contain only letters.");
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
