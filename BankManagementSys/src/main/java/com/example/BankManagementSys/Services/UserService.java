package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.User;
import com.example.BankManagementSys.Reposityories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import java.util.Optional;
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
            // ✅ Ensure required fields are not null or empty
            if (user.getName() == null || user.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Full name is required.");
            }
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Email is required.");
            }
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("Password is required.");
            }
            if (user.getBirthdate() == null) {
                throw new IllegalArgumentException("Birthdate is required.");
            }
            if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
                throw new IllegalArgumentException("Username is required.");
            }


            // ✅ Validate Email Format
            if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
                throw new IllegalArgumentException("Invalid email format.");
            }

            // ✅ Check if email already exists
            Optional<User> existingEmail = userRepository.findUsersByEmailEquals(user.getEmail());
            if (existingEmail.isPresent()) {
                throw new IllegalArgumentException("Email already exists.");
            }

            // ✅ Validate Name Format (Only letters)
            if (!NAME_PATTERN.matcher(user.getName()).matches()) {
                throw new IllegalArgumentException("Name must contain only letters.");
            }

            // ✅ Check if Username already exists
            Optional<User> existingUser = userRepository.findUserByUserNameEquals(user.getUserName());
            if (existingUser.isPresent()) {
                throw new IllegalArgumentException("Username already exists.");
            }

        } catch (IllegalArgumentException e) {
            System.err.println("❌ Validation failed: " + e.getMessage());
            throw e;
        }
    }


    public int calculateAge(Date birthdate) {
        return userRepository.calculateAge(birthdate);
    }

    public List<User> findUsersByProvider(String provider) {
        return userRepository.getUsersWithSpecificProvider(provider);
    }

    // Security
    public User getUser(String userName) {
        User existingUser = userRepository.findByUserName(userName);
        if (existingUser != null) {
            System.out.println("getUser:**********" + existingUser.getUserName() + " ps" + existingUser.getPassword());
            return existingUser;
        }
        return null;
    }
}
