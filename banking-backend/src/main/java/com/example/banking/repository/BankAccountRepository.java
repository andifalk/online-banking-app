package com.example.banking.repository;

import com.example.banking.model.BankAccount;
import com.example.banking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    Optional<BankAccount> findByAccountNumber(String accountNumber);

    Optional<BankAccount> findByAccountNumberAndUser(String accountNumber, User user);

    List<BankAccount> findAllByUser(User user);
}
