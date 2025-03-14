package com.example.BankManagementSys.Services;
import com.example.BankManagementSys.Entities.Employee;
import com.example.BankManagementSys.Entities.User;
import com.example.BankManagementSys.Reposityories.CustomerRepository;
import com.example.BankManagementSys.Reposityories.EmployeeRepository;
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
    private EmployeeRepository employeeRepository;  // ✅ Inject Employee Repository
    @Autowired
    private CustomerRepository customerRepository;  // ✅ Inject Customer Repository
    @Autowired
    private UserService userService;
    @Autowired
    private EmployeeService employeeService;
    public LoginData login(String userName, String password) {
        User existingUser = userService.getUser(userName);

        if (existingUser != null) {
            System.out.println("login: User found: " + existingUser.getUserName());
            if (existingUser.getPassword().equals(password)) {
                System.out.println("login: Password matches for user: " + userName);

                // ✅ Determine if user is an employee or a customer
                String role;
                if (employeeRepository.findByUserName(userName).isPresent()) {
                    role = "employee";
                } else if (customerRepository.findByUserName(userName).isPresent()) {
                    role = "customer";
                } else {
                    role = "unknown"; // Should never happen
                }

                String token = jwtUtils.generateJWTToken(userName); // ✅ Generate Token
                System.out.println("login: Token generated: " + token);

                return LoginData.builder()
                        .token(token)
                        .role(role)  // ✅ Include role in response
                        .build();
            } else {
                System.out.println("login: Password mismatch for user: " + userName);
            }
        } else {
            System.out.println("login: No user found with username: " + userName);
        }
        return null;
    }

}
