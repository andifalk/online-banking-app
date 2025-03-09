package com.example.banking.api;

import com.example.banking.model.BankAccount;
import com.example.banking.model.Transaction;
import com.example.banking.model.User;
import com.example.banking.service.BankAccountService;
import com.example.banking.service.TransactionService;
import com.example.banking.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.banking.model.TransactionType.*;

@RequestMapping("/api/admin")
@RestController
public class AdminController {

    private final UserService userService;
    private final TransactionService transactionService;
    private final BankAccountService bankAccountService;

    public AdminController(UserService userService, TransactionService transactionService, BankAccountService bankAccountService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.bankAccountService = bankAccountService;
    }

    // Get All Users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    // Delete a User by ID
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            userService.deleteById(id);
            return ResponseEntity.ok("User deleted successfully");
        }
        return ResponseEntity.badRequest().body("User not found");
    }

    // Get All Transactions
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    // Delete a Transaction by ID
    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable Long id) {
        Optional<Transaction> transaction = transactionService.findById(id);
        if (transaction.isPresent()) {
            transactionService.deleteById(id);
            return ResponseEntity.ok("Transaction deleted successfully");
        }
        return ResponseEntity.badRequest().body("Transaction not found");
    }

    @GetMapping("/account-summary/{accountNumber}")
    public ResponseEntity<Map<String, BigDecimal>> getAccountSummary(@PathVariable String accountNumber) {
        Optional<BankAccount> account = bankAccountService.findByAccountNumber(accountNumber);
        if (account.isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
            BankAccount bankAccount = account.get();
            List<Transaction> transactions = transactionService.findByBankAccount(bankAccount);

            BigDecimal totalDeposits = transactions.stream()
                    .filter(tx -> DEPOSIT.equals(tx.getTransactionType()))
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalWithdrawals = transactions.stream()
                    .filter(tx -> WITHDRAW.equals(tx.getTransactionType()))
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalTransfers = transactions.stream()
                    .filter(tx -> TRANSFER.equals(tx.getTransactionType()))
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal currentBalance = bankAccount.getBalance();

            return ResponseEntity.ok(Map.of(
                    "Total Deposits", totalDeposits,
                    "Total Withdrawals", totalWithdrawals,
                    "Total Transfers", totalTransfers,
                    "Final Balance", currentBalance
            ));
        }


    }
}
