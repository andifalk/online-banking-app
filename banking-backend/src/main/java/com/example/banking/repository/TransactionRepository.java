package com.example.banking.repository;

import com.example.banking.model.BankAccount;
import com.example.banking.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByBankAccount(BankAccount account);
}
