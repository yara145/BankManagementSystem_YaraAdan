package com.example.BankManagementSys.Reposityories;

import com.example.BankManagementSys.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    // Find users by exact username
    List<User> findUsersByUserNameEquals(String userName);

    // Find users by username pattern
    @Query(value = "SELECT * FROM users WHERE user_name LIKE %:provider%", nativeQuery = true)
    List<User> getUsersWithSpecificProvider(@Param("provider") String provider);

    // Find users by exact ID number
    List<User> findUsersByIdNumberEquals(String idNumber);

    // Find users by ID number pattern
    @Query(value = "SELECT * FROM users WHERE id_number LIKE %:provider%", nativeQuery = true)
    List<User> getUsersWithSpecificIdProvider(@Param("provider") String provider);

    // Calculate the age of a user by their birthdate
    @Query(value = "SELECT TIMESTAMPDIFF(YEAR, :birthdate, CURDATE())", nativeQuery = true)
    Integer calculateAge(@Param("birthdate") Date birthdate);
}
