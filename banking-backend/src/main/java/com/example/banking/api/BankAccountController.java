package com.example.banking.api;

import com.example.banking.model.BankAccount;
import com.example.banking.service.BankAccountService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {
    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping
    public BankAccount createAccount(@RequestBody BankAccount account) {
        return bankAccountService.createAccount(account);
    }

    @GetMapping
    public List<BankAccount> getAllAccounts() {
        return bankAccountService.getAllAccounts();
    }

    @PostMapping("/deposit")
    public BankAccount deposit(@RequestParam String accountNumber, @RequestParam BigDecimal amount) {
        return bankAccountService.deposit(accountNumber, amount);
    }
}
