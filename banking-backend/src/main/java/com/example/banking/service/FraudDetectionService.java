package com.example.banking.service;

import com.example.banking.model.BankAccount;
import com.example.banking.model.Transaction;
import com.example.banking.model.User;
import com.example.banking.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class FraudDetectionService {

    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;

    public FraudDetectionService(TransactionRepository transactionRepository, NotificationService notificationService) {
        this.transactionRepository = transactionRepository;
        this.notificationService = notificationService;
    }

    public boolean isSuspiciousTransaction(BankAccount senderAccount, User user, BigDecimal amount) {
        // Check if transaction is significantly larger than the user's average transaction
        List<Transaction> userTransactions = transactionRepository.findByBankAccount(senderAccount);

        // Calculate average transaction amount
        BigDecimal averageAmount = userTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(userTransactions.size()), RoundingMode.HALF_UP);

        boolean isSuspicious = (amount.compareTo(averageAmount.multiply(BigDecimal.valueOf(5)))) > 0;

        if (isSuspicious) {
            notificationService.sendFraudAlert(user.getEmail(), amount);
        }

        return isSuspicious;
    }
}
