package com.example.BankManagementSys.Services;
import com.example.BankManagementSys.Entities.User;
import com.example.BankManagementSys.Reposityories.UserRepository;
import com.example.BankManagementSys.Security.JWTUtils;
import com.example.BankManagementSys.components.LoginData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private UserService userService;

    public LoginData login(String userName, String password) {
        User existingUser = this.userService.getUser(userName);
        if (existingUser != null) {
            System.out.println("login: User found: " + existingUser.getUserName());
            if (existingUser.getPassword().equals(password)) {
                System.out.println("login: Password matches for user: " + userName);
                String token = jwtUtils.generateJWTToken(userName); // Token generation
                System.out.println("login: Token generated: " + token);
                return LoginData.builder().token(token).build();
            } else {
                System.out.println("login: Password mismatch for user: " + userName);
            }
        } else {
            System.out.println("login: No user found with username: " + userName);
        }
        return null;
    }


}
