package com.example.BankManagementSys;

import com.example.BankManagementSys.Entities.*;
import com.example.BankManagementSys.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
@EnableScheduling // Enable scheduled jobs
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
	@Autowired
	private  TransferTransactionService transferService;

	// ✅ Injects CurrencyExchangeService to use currency conversion methods
	@Autowired
	private CurrencyExchangeService currencyExchangeService;


	@Override
	public void run(String... args) throws Exception {

		System.out.println("*******create customer 1 *****\n");
		Customer customer = new Customer();
		customer.setUserName("Adan123");
		customer.setPassword("1234");
		customer.setEmail("adanorabi12@gmail.com");
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

		customer.setName("Adan");
		// Add customer using the service
		customerService.addNewCustomer(customer);

		System.out.println("*******create customer 2 *****\n");
		Customer customer2 = new Customer();
		customer2.setUserName("Anne2001");
		customer2.setPassword("2001");
		customer2.setEmail("anne@gmail.com");
		customer2.setAddress("Haifa");

		// Parse birthdate from string
		String birthdateString2 = "15.2.1990";
		SimpleDateFormat formatter2 = new SimpleDateFormat("dd.MM.yyyy");
		try {
			Date birthdate2 = formatter.parse(birthdateString2);
			customer2.setBirthdate(birthdate2);
		} catch (ParseException e) {
			System.err.println("Invalid birthdate format: " + e.getMessage());
		}

		customer2.setName("Anne");

		// Add customer using the service
		customerService.addNewCustomer(customer2);


		System.out.println("*******create bank *****\n");
		Bank bank = new Bank();
		bank.setName("MyBank");
		bankService.addBank(bank);

		System.out.println("*******create branch *****\n");
		Branch branch = new Branch();
		branch.setName("first branch");
		branch.setLocation("haifa");
		branchService.createNewBranch(branch);
		bankService.addBranchToBank(bank.getId(), branch);



		System.out.println("*******bank account 1 *****\n");
		BankAccount bankAccount = new BankAccount();
		bankAccount.setType("personal");
		bankAccountService.createNewBankAccount(bankAccount);

		customerService.addBankAccountToCustomer(customer.getIdCode(), bankAccount);

		System.out.println("*******bank account 2 *****\n");
		BankAccount bankAccount3 = new BankAccount();
		bankAccount3.setType("personal");
		bankAccountService.createNewBankAccount(bankAccount3);

		customerService.addBankAccountToCustomer(customer.getIdCode(), bankAccount3);



		System.out.println("*******bank account 3*****\n");
		BankAccount bankAccount2 = new BankAccount();
		bankAccount2.setType("personal");
		bankAccountService.createNewBankAccount(bankAccount2);
		customerService.addBankAccountToCustomer(customer2.getIdCode(), bankAccount2);

		System.out.println("******* Adding Bank Account to Branch *****\n");
		branchService.addBankAccountToBranch(branch.getId(), bankAccount);
		branchService.addBankAccountToBranch(branch.getId(), bankAccount2);
		branchService.addBankAccountToBranch(branch.getId(), bankAccount3);
		System.out.println("******* Create Employee 1 *****\n");
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

		employee.setName("yara");


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


// ✅ Ensure the bank account exists
		BankAccount existingBankAccount = bankAccountService.getBankAccountById(1);
		if (existingBankAccount == null) {
			System.err.println("❌ Error: Bank account does not exist. Cannot proceed with transactions.");
			return;
		}

		//System.out.println("*********** deposits **********");

// ✅ Deposit 1
		DepositTransaction deposit = new DepositTransaction();
		deposit.setDespositAmount(BigDecimal.valueOf(20000));
		deposit.setCurrencyCode("EUR");
		deposit.setDescription(deposit.getDespositAmount().toString()+" "+deposit.getCurrencyCode());
		System.out.println(depositService.addNewDepositTransaction(deposit));
		System.out.println("Connect deposit to the bank account");
		System.out.println(depositService.connectTransactionToBank(deposit, existingBankAccount.getId()));


		System.out.println("*********** withdrawal **********");

// ✅ Withdrawal 1
		WithdrawalTransaction withdrawal = new WithdrawalTransaction();
		withdrawal.setCurrencyCode("EUR");
		withdrawal.setWithdrawalAmount(BigDecimal.valueOf(10000));
		withdrawal.setDescription(withdrawal.getWithdrawalAmount().toString()+" "+withdrawal.getCurrencyCode());
		System.out.println(withdrawalService.addNewWithdrawalTransaction(withdrawal));
		System.out.println(withdrawalService.connectTransactionToBank(withdrawal, existingBankAccount.getId()));



// ✅ Deposit 2
		DepositTransaction deposit2 = new DepositTransaction();
		deposit2.setDespositAmount(BigDecimal.valueOf(40000));
		System.out.println(depositService.addNewDepositTransaction(deposit2));
		System.out.println(depositService.getDepoistById(deposit2.getTransactionId())); // 🔥 Use actual ID
		deposit2.setDescription(deposit2.getDespositAmount().toString()+" "+deposit2.getCurrencyCode());
		System.out.println("Connect deposit to the bank account");
		System.out.println(depositService.connectTransactionToBank(deposit2, existingBankAccount.getId()));

// ✅ Withdrawal 2
		WithdrawalTransaction withdrawal2 = new WithdrawalTransaction();
		withdrawal2.setWithdrawalAmount(BigDecimal.valueOf(1000));
		withdrawal2.setDescription(withdrawal2.getWithdrawalAmount().toString()+" "+withdrawal2.getCurrencyCode());
		System.out.println(withdrawalService.addNewWithdrawalTransaction(withdrawal2));
		System.out.println(withdrawalService.connectTransactionToBank(withdrawal2, existingBankAccount.getId()));

		// ✅ Deposit 3
		DepositTransaction deposit3 = new DepositTransaction();
		deposit3.setDespositAmount(BigDecimal.valueOf(3000));
		deposit3.setDescription(deposit3.getDespositAmount().toString()+" "+deposit3.getCurrencyCode());
		System.out.println(depositService.addNewDepositTransaction(deposit3));
		System.out.println(depositService.getDepoistById(deposit3.getTransactionId())); // 🔥 Use actual ID
		System.out.println("Connect deposit to the bank account");
		System.out.println(depositService.connectTransactionToBank(deposit3, existingBankAccount.getId()));


// ✅ Withdrawal 3
		WithdrawalTransaction withdrawal3 = new WithdrawalTransaction();
		withdrawal3.setWithdrawalAmount(BigDecimal.valueOf(900));
		withdrawal3.setDescription(withdrawal3.getWithdrawalAmount().toString()+" "+withdrawal3.getCurrencyCode());
		System.out.println(withdrawalService.addNewWithdrawalTransaction(withdrawal3));
		System.out.println(withdrawalService.connectTransactionToBank(withdrawal3, existingBankAccount.getId()));


// Transfer 1

		System.out.println("***********Transfer **********\n");
		TransferTransaction transfer = new TransferTransaction();
		transfer.setReceiverBankCode(1);
		transfer.setTransferBranchCode(1);
		transfer.setReceiverAccountNum(2);
		transfer.setBankAccount(existingBankAccount);
		transfer.setAmount(BigDecimal.valueOf(500));
		transferService.addNewTransferTransaction(transfer);
		transferService.connectTransactionToBank(transfer,1);




//		System.out.println("*********** Loan **********\n");
// //✅ Loan
//		Loan loan = new Loan();
//		loan.setLoanName("Personal Loan for Yara");
//		loan.setInterestRate(0.3);
//		loan.setNumberOfPayments(5);
//		loan.setLoanAmount(BigDecimal.valueOf(10000));
//		loan.setRemainingBalance(loan.getLoanAmount().doubleValue()); // ✅ Set correct balance
//		System.out.println(loanService.addNewLoan(loan));
//		System.out.println("*********** Print the Loan **********\n" + loan.getTransactionId());
//		System.out.println(loanService.connectLoanToBank(loan, existingBankAccount.getId()));
//		System.out.println("*********** Loan Payments are scheduled  **********\n");





	}
}
//		System.out.println("✅ Testing API Connection...\n");
//		BigDecimal usdRate = currencyExchangeService.getExchangeRateForCurrency("USD");
//		System.out.println("Exchange Rate for USD: " + usdRate);

