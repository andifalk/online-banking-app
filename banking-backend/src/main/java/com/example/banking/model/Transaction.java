package com.example.banking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "bank_transaction")
public class Transaction extends AbstractPersistable<Long> {

    @Enumerated(STRING)
    private TransactionType transactionType;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private LocalDateTime timestamp;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "account_id")
    private BankAccount bankAccount;

    public Transaction() {
    }

    public Transaction(TransactionType transactionType, BigDecimal amount, LocalDateTime timestamp, BankAccount bankAccount) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.timestamp = timestamp;
        this.bankAccount = bankAccount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }
}
