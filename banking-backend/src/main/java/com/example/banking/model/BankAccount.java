package com.example.banking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.math.BigDecimal;

import static jakarta.persistence.EnumType.STRING;

@Entity
public class BankAccount extends AbstractPersistable<Long> {

    @Size(min = 10, max = 50)
    private String accountNumber;

    @NotNull
    private BigDecimal balance;

    @NotNull
    @Enumerated(STRING)
    private AccountType accountType; // SAVINGS, CHECKING

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public BankAccount() {
    }

    public BankAccount(String accountNumber, BigDecimal balance, AccountType accountType, User user) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountType = accountType;
        this.user = user;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
