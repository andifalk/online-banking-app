package com.example.banking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.example.banking.model.TransactionStatus.*;
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

    @Enumerated(STRING)
    private TransactionStatus transactionStatus;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "account_id")
    private BankAccount bankAccount;

    public Transaction() {
        this.transactionStatus = APPROVED;
    }

    public Transaction(TransactionType transactionType, TransactionStatus transactionStatus, BigDecimal amount, LocalDateTime timestamp, BankAccount bankAccount) {
        this.transactionType = transactionType;
        this.transactionStatus = transactionStatus;
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

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
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

    public void markPending() {
        this.transactionStatus = PENDING;
    }

    public void approve() {
        this.transactionStatus = APPROVED;
    }

    public void reject() {
        this.transactionStatus = REJECTED;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Transaction that = (Transaction) o;
        return transactionType == that.transactionType && Objects.equals(amount, that.amount) && Objects.equals(timestamp, that.timestamp) && transactionStatus == that.transactionStatus && Objects.equals(bankAccount, that.bankAccount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), transactionType, amount, timestamp, transactionStatus, bankAccount);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "amount=" + amount +
                ", transactionType=" + transactionType +
                ", timestamp=" + timestamp +
                ", transactionStatus=" + transactionStatus +
                ", bankAccount=" + bankAccount +
                '}';
    }
}
