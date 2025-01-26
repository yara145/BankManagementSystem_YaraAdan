package com.example.BankManagementSys;

import com.example.BankManagementSys.Entities.*;
import com.example.BankManagementSys.Enums.TransferStatus;
import com.example.BankManagementSys.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
	BranchService branchService;
	@Autowired
	EmployeeService employeeService;
	@Autowired
	ManyToManyRelationService manyToManyRelationService;
	@Autowired
	BankService bankService;
	@Autowired
	TransactionService transactionService;
	@Autowired
	TransferTransactionService transferService;
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
		BankAccount bankAccount=new BankAccount();
		bankAccount.setType("personal");
		bankAccountService.createNewBankAccount(bankAccount);

		customerService.addBankAccountToCustomer(customer.getIdCode(),bankAccount);

		Bank bank=new Bank();
		bank.setName("MyBank");
		bankService.addBank(bank);

		Branch branch =new Branch();
		branch.setName("first branch");
		branch.setLocation("haifa");
		branchService.createNewBranch(branch);
		bankService.addBranchToBank(bank.getId(),branch);


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
		String startDateString = "01.01.2023"; // Adjust as needed
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



		System.out.println("*************************TRANSACTIONS*******************************\n" );

		System.out.println("\n");
		System.out.println("\n");
		TransferTransaction transfer = new TransferTransaction();
		transfer.setAmount(500);
		transfer.setDescription("This is my first transfer");
		transfer.setTransferAccountNum(112);
		transfer.setTransferBranchCode(2009);
		transfer.setTransferBankCode(632);
		transfer.setTransferStatus(TransferStatus.PENDING);
		transfer.setTransferDate(new Date());
		transfer.setTransferName("Transfer to Yara Ghaben");

//** Transfer *****
		System.out.println("Lets add transfer :   " + transfer);
		this.transferService.addNewTransferTransaction(transfer);

		System.out.println("print bank account that did the transfer \n");
		System.out.println(transfer.getTransactionId());
		System.out.println(transfer.getBankAccount());


		System.out.println("************ update transfer amount \n");
		transfer.setAmount(90);
//		System.out.println(this.transferService.updateTransferTransaction(transfer));
//		System.out.println(transfer);

		System.out.println("get transfer by id \n");
		//System.out.println(this.transferService.getTransferById(7));
		System.out.println(this.transferService.getTransferById(1));

		transfer.setAmount(3000);
		System.out.println("lets update the transfer \n");
		System.out.println(this.transferService.updateTransferTransaction(transfer));
		System.out.println("Connect transfer to the bank account \n");
		System.out.println(this.transferService.	connectTransactionToBank(transfer,1));


		System.out.println("*********** deposit **********");
		DepositTransaction deposit = new DepositTransaction();
		deposit.setDespositAmount(344);
		System.out.println(	depositService.addNewDepositTransaction(deposit));
		System.out.println("Connect deposit to the bank account \n");
		System.out.println(this.depositService.	connectTransactionToBank(deposit,1));


		DepositTransaction deposit2 = new DepositTransaction();
		deposit2.setDespositAmount(222);
		System.out.println(	depositService.addNewDepositTransaction(deposit2));
		System.out.println(depositService.getDepoistById(2));

		System.out.println("Connect deposit to the bank account \n");
		System.out.println(this.depositService.	connectTransactionToBank(deposit2,1));


		System.out.println("*********** withdrawal **********");
		WithdrawalTransaction withdrawal = new WithdrawalTransaction();
		withdrawal.setWithdrawalAmount(455);
		System.out.println(withdrawalService.addNewWithdrawalTransaction(withdrawal));
		System.out.println(this.withdrawalService.	connectTransactionToBank(withdrawal,1));


		System.out.println("*********** Loan **********");
		Loan loan = new Loan();
		loan.setEndPaymentDate(new Date());
		loan.setInterestRate(0.3);
		loan.setLoanAmount(12000);
		loan.setStartPaymentDate(new Date());
		System.out.println(loanService.addNewLoan(loan));
		System.out.println(this.loanService.connectLoanToBank(loan,1));

//		System.out.println("*********** Loan Payment **********");
//		LoanPayment loanPayment = new LoanPayment();
//		loanPayment.setPaymentAmount(222);
//		loanPayment.setPaymentDate(new Date());
//
//		System.out.println(this.loanPaymentService.addLoanPayment(loanPayment, 1));
//		System.out.println("print all transactions \n");
//		List<Transaction> allTransactions = this.transactionService.getAllTransactions();
//		System.out.println(allTransactions);



	}
	}
