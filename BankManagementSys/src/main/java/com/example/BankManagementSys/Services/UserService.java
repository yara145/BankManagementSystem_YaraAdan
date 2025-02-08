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



            // ✅ Validate Email Format & Uniqueness
            if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
                throw new IllegalArgumentException("Invalid email format.");
            }
            if (!userRepository.findUsersByEmailEquals(user.getEmail()).isEmpty()) {
                throw new IllegalArgumentException("Email already exists.");
            }

            // ✅ Validate First & Last Name
            if (!NAME_PATTERN.matcher(user.getFirstName()).matches()) {
                throw new IllegalArgumentException("name must contain only letters.");
            }

            // ✅ Validate Username Uniqueness
            if (!userRepository.findUsersByUserNameEquals(user.getUserName()).isEmpty()) {
                throw new IllegalArgumentException("Username already exists.");
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Validation failed: " + e.getMessage());
            throw e;
        }
    }

    public int calculateAge(Date birthdate) {
        return userRepository.calculateAge(birthdate);
    }

    public List<User> findUsersByProvider(String provider) {
        return userRepository.getUsersWithSpecificProvider(provider);
    }


    //Secuerity
    public User getUser(String userName){
        User existingUser=userRepository.findByUserName(userName);
        if(existingUser!=null){
            System.out.println("getUser:**********"+existingUser.getUserName()+" ps"+ existingUser.getPassword());
            return existingUser;
        }
        return null;
    }



}
