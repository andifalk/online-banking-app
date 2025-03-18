package com.example.banking.api;

import com.example.banking.model.Transaction;
import com.example.banking.model.TransactionType;
import com.example.banking.model.User;
import com.example.banking.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequestMapping("/api/transactions")
@RestController
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public Transaction transfer(@RequestParam String senderAccountNumber,
                                @RequestParam String receiverAccountNumber,
                                @RequestParam BigDecimal amount,
                                @AuthenticationPrincipal User user) {
        return transactionService.transferMoney(user, senderAccountNumber, receiverAccountNumber, amount);
    }

    @PostMapping("/deposit")
    public Transaction deposit(@RequestParam String accountNumber, @RequestParam BigDecimal amount) {
        return transactionService.createTransaction(accountNumber, TransactionType.DEPOSIT, amount);
    }

    @PostMapping("/withdraw")
    public Transaction withdraw(@RequestParam String accountNumber, @RequestParam BigDecimal amount) {
        return transactionService.createTransaction(accountNumber, TransactionType.WITHDRAW, amount);
    }

    @GetMapping("/{accountNumber}")
    public List<Transaction> getTransactionHistory(@PathVariable String accountNumber) {
        return transactionService.getTransactionsByAccount(accountNumber);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Transaction>> getTransactionHistory(@AuthenticationPrincipal User user) {
        List<Transaction> transactions = transactionService.getTransactionHistory(user);
        return ResponseEntity.ok(transactions);
    }
}
