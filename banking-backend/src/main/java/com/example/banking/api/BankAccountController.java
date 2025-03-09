package com.example.banking.api;

import com.example.banking.model.BankAccount;
import com.example.banking.model.User;
import com.example.banking.service.BankAccountService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {
    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping
    public BankAccount createAccount(@AuthenticationPrincipal User user, @RequestBody BankAccount account) {
        return bankAccountService.createAccount(account, user);
    }

    @GetMapping
    public List<BankAccount> getAllMyAccounts(@AuthenticationPrincipal User user) {
        return bankAccountService.getAllMyAccounts(user);
    }
}
