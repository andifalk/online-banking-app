package com.example.banking.service;

import com.example.banking.model.BankAccount;
import com.example.banking.model.User;
import com.example.banking.repository.BankAccountRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@Transactional(readOnly = true)
@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Transactional
    public BankAccount createAccount(BankAccount account, User user) {
        account.setUser(user);
        return bankAccountRepository.save(account);
    }

    public List<BankAccount> getAllMyAccounts(User user) {
        return bankAccountRepository.findAllByUser(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<BankAccount> getAllAccounts() {
        return bankAccountRepository.findAll();
    }

    public Optional<BankAccount> getAccountById(Long id) {
        return bankAccountRepository.findById(id);
    }

    public Optional<BankAccount> findByAccountNumberForUser(String accountNumber, User user) {
        return bankAccountRepository.findByAccountNumberAndUser(accountNumber, user);
    }

    public Optional<BankAccount> findByAccountNumber(String accountNumber) {
        return bankAccountRepository.findByAccountNumber(accountNumber);
    }

    @Transactional
    public void deleteAccount(Long id) {
        bankAccountRepository.deleteById(id);
    }

    @Transactional
    public BankAccount deposit(String accountNumber, BigDecimal amount) {
        Optional<BankAccount> account = bankAccountRepository.findByAccountNumber(accountNumber);
        if (account.isEmpty()) {
            throw new RuntimeException("Account not found");
        } else {
            account.get().setBalance(account.get().getBalance().add(amount));
            return bankAccountRepository.save(account.get());
        }
    }
}
