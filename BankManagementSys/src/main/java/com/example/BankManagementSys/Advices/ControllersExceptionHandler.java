package com.example.BankManagementSys.Advices;

import com.example.BankManagementSys.Exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllersExceptionHandler {

    // ✅ Handle Bank Account Not Found
    @ExceptionHandler(BankAccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleBankAccountNotFoundException(BankAccountNotFoundException e) {
        System.out.println("BankAccountNotFoundException fired.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bank account not found.");
    }

    // ✅ Handle Branch Not Found
    @ExceptionHandler(BranchNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleBranchNotFoundException(BranchNotFoundException e) {
        System.out.println("BranchNotFoundException fired.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Branch not found.");
    }

    // ✅ Handle Customer Not Found
    @ExceptionHandler(CustomerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleCustomerNotFoundException(CustomerNotFoundException e) {
        System.out.println("CustomerNotFoundException fired.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found.");
    }

    // ✅ Handle Employee Not Found
    @ExceptionHandler(EmployeeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleEmployeeNotFoundException(EmployeeNotFoundException e) {
        System.out.println("EmployeeNotFoundException fired.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found.");
    }

    // ✅ Handle Illegal State (e.g., deleting an entity when constraints prevent it)
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException e) {
        System.out.println("IllegalStateException fired.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    // ✅ Handle Generic Exception (Fallback)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleGeneralException(Exception e) {
        System.out.println("A general exception occurred: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
    }
}
