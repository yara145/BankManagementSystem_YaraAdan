package com.example.BankManagementSys;

import com.example.BankManagementSys.Entities.*;
import com.example.BankManagementSys.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class BankManagementSysApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(BankManagementSysApplication.class, args);
	}

	@Autowired
	private CustomerService customerService;
	@Autowired
	private BankAccountService bankAccountService;
	@Autowired
	private BranchService branchService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private ManyToManyRelationService manyToManyRelationService;
	@Autowired
	private BankService bankService;
	@Autowired
	private TransactionService transactionService;
	@Autowired
	private DepositTransactionService depositService;
	@Autowired
	private WithdrawalTransactionService withdrawalService;
	@Autowired
	private LoanService loanService;
	@Autowired
	private LoanPaymentService loanPaymentService;

	@Override
	public void run(String... args) throws Exception {

		System.out.println("*******create customer*****");
		Customer customer = new Customer();
		customer.setUserName("Adan123");
		customer.setPassword("1234");
		customer.setEmail("adan@gmail.com");
		customer.setAddress("Nazareth");

		// Parse birthdate from string
		String birthdateString = "21.4.2000";
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		try {
			Date birthdate = formatter.parse(birthdateString);
			customer.setBirthdate(birthdate);
		} catch (ParseException e) {
			System.err.println("Invalid birthdate format: " + e.getMessage());
		}

		customer.setIdNumber("111222333");
		customer.setFirstName("Adan");
		customer.setLastName("Orabi");

		// Add customer using the service
		customerService.addNewCustomer(customer);

		System.out.println("*******bank account*****");
		BankAccount bankAccount = new BankAccount();
		bankAccount.setType("personal");
		bankAccountService.createNewBankAccount(bankAccount);

		customerService.addBankAccountToCustomer(customer.getIdCode(), bankAccount);

		Bank bank = new Bank();
		bank.setName("MyBank");
		bankService.addBank(bank);

		Branch branch = new Branch();
		branch.setName("first branch");
		branch.setLocation("haifa");
		branchService.createNewBranch(branch);
		bankService.addBranchToBank(bank.getId(), branch);

		branchService.addBankAccountToBranch(branch.getId(), bankAccount);

		Employee employee = new Employee();
		employee.setUserName("yara123");
		employee.setPassword("1234");
		employee.setEmail("yara@gmail.com");
		employee.setAddress("Nazareth");

		String birthdateString1 = "21.4.2000";
		SimpleDateFormat formatter1 = new SimpleDateFormat("dd.MM.yyyy");

		try {
			Date birthdate = formatter1.parse(birthdateString1);
			employee.setBirthdate(birthdate);
		} catch (ParseException e) {
			System.err.println("Invalid birthdate format: " + e.getMessage());
		}

		employee.setIdNumber("111222444");
		employee.setFirstName("yara");
		employee.setLastName("ghaben");

		// Set the work start date
		String startDateString = "01.01.2023";
		try {
			Date startDate = formatter1.parse(startDateString);
			employee.setStartDate(startDate);
		} catch (ParseException e) {
			System.err.println("Invalid start date format: " + e.getMessage());
		}

		employeeService.addNewEmployee(employee);
		System.out.println("**assign employee to bank account and branch**");

		// Add employee to the bank account
		manyToManyRelationService.addEmployeeToBankAccount(employee.getIdCode(), bankAccount.getId());

		// Add employee to the branch
		manyToManyRelationService.addEmployeeToBranch(employee.getIdCode(), branch.getId());

		System.out.println("*************************TRANSACTIONS*******************************\n");
		System.out.println("*********** deposit **********");

// ✅ Ensure the bank account exists
		BankAccount existingBankAccount = bankAccountService.getBankAccountById(1);
		if (existingBankAccount == null) {
			System.err.println("❌ Error: Bank account does not exist. Cannot proceed with transactions.");
			return;
		}

// ✅ Deposit 1
		DepositTransaction deposit = new DepositTransaction();
		deposit.setDespositAmount(BigDecimal.valueOf(344));
		deposit.setBankAccount(existingBankAccount); // 🔥 Link to bank account
		System.out.println(depositService.addNewDepositTransaction(deposit));
		System.out.println("Connect deposit to the bank account");
		System.out.println(depositService.connectTransactionToBank(deposit, existingBankAccount.getId()));

// ✅ Deposit 2
		DepositTransaction deposit2 = new DepositTransaction();
		deposit2.setDespositAmount(BigDecimal.valueOf(222));
		deposit2.setBankAccount(existingBankAccount); // 🔥 Link to bank account
		System.out.println(depositService.addNewDepositTransaction(deposit2));
		System.out.println(depositService.getDepoistById(deposit2.getTransactionId())); // 🔥 Use actual ID
		System.out.println("Connect deposit to the bank account");
		System.out.println(depositService.connectTransactionToBank(deposit2, existingBankAccount.getId()));

		System.out.println("*********** withdrawal **********");

// ✅ Withdrawal
		WithdrawalTransaction withdrawal = new WithdrawalTransaction();
		withdrawal.setWithdrawalAmount(BigDecimal.valueOf(455));
		withdrawal.setBankAccount(existingBankAccount); // 🔥 Link to bank account
		System.out.println(withdrawalService.addNewWithdrawalTransaction(withdrawal));
		System.out.println(withdrawalService.connectTransactionToBank(withdrawal, existingBankAccount.getId()));

		System.out.println("*********** Loan **********\n");

// ✅ Loan
		Loan loan = new Loan();
		loan.setEndPaymentDate(new Date());
		loan.setLoanName("Personal Loan for Yara");
		loan.setInterestRate(0.3);
		loan.setLoanAmount(BigDecimal.valueOf(12000));
		loan.setRemainingBalance(loan.getLoanAmount().doubleValue()); // ✅ Set correct balance
		loan.setStartPaymentDate(new Date());
		loan.setBankAccount(existingBankAccount); // 🔥 Link to bank account

		System.out.println(loanService.addNewLoan(loan));
		System.out.println("*********** Print the Loan **********\n" + loan.getTransactionId());
		System.out.println(loanService.connectLoanToBank(loan, existingBankAccount.getId()));


		System.out.println("*********** Loan Payment **********");

// ✅ Loan Payment
		LoanPayment loanPayment = new LoanPayment();
		loanPayment.setPaymentAmount(222);
		loanPayment.setLoan(loan); // 🔥 Link to loan

		System.out.println(loanPaymentService.addLoanPayment(loanPayment, loan.getTransactionId()));
		System.out.println("Print all transactions");
		List<Transaction> allTransactions = transactionService.getAllTransactions();
		System.out.println(allTransactions);

	}
}
