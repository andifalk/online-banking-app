package com.example.banking.service;

import com.example.banking.model.BankAccount;
import com.example.banking.model.Transaction;
import com.example.banking.model.TransactionType;
import com.example.banking.model.User;
import com.example.banking.repository.BankAccountRepository;
import com.example.banking.repository.TransactionRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.banking.model.TransactionType.*;

@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@Transactional(readOnly = true)
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final NotificationService notificationService;
    private final FraudDetectionService fraudDetectionService;
    private final BankAccountService bankAccountService;

    public TransactionService(TransactionRepository transactionRepository, BankAccountRepository bankAccountRepository, NotificationService notificationService, FraudDetectionService fraudDetectionService, BankAccountService bankAccountService) {
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.notificationService = notificationService;
        this.fraudDetectionService = fraudDetectionService;
        this.bankAccountService = bankAccountService;
    }

    @Transactional
    public Transaction transferMoney(User user, String senderAccountNumber, String receiverAccountNumber, BigDecimal amount) {
        Optional<BankAccount> sender = bankAccountRepository.findByAccountNumber(senderAccountNumber);
        Optional<BankAccount> receiver = bankAccountRepository.findByAccountNumber(receiverAccountNumber);

        if (sender.isEmpty() || receiver.isEmpty()) {
            throw new RuntimeException("One or both accounts not found");
        } else {
            BankAccount senderAccount = sender.get();
            BankAccount receiverAccount = receiver.get();

            if (senderAccount.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient funds");
            }


            Transaction transaction = new Transaction();
            transaction.setTransactionType(TRANSFER);
            transaction.setAmount(amount);
            transaction.setTimestamp(LocalDateTime.now());
            transaction.setBankAccount(senderAccount);

            if (fraudDetectionService.isSuspiciousTransaction(senderAccount, user, amount)) {
                transaction.markPending();
            } else {
                transaction.approve();
                senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
                receiverAccount.setBalance(receiverAccount.getBalance().add(amount));
                bankAccountRepository.save(senderAccount);
                bankAccountRepository.save(receiverAccount);
            }

            Transaction savedTransaction = transactionRepository.save(transaction);

            // Notify sender
            String senderMessage = "Dear User,\n\nYou transferred $" + amount + " to " + receiverAccount.getAccountNumber() +
                    ". Your new balance is $" + senderAccount.getBalance() + ".\n\nThank you for banking with us!";
            notificationService.sendTransactionEmail("sender@example.com", "Money Transfer Alert", senderMessage);

            // Notify receiver
            String receiverMessage = "Dear User,\n\nYou received $" + amount + " from " + senderAccount.getAccountNumber() +
                    ". Your new balance is $" + receiverAccount.getBalance() + ".\n\nThank you for banking with us!";
            notificationService.sendTransactionEmail("receiver@example.com", "Money Received Alert", receiverMessage);

            return savedTransaction;
        }
    }

    public List<Transaction> getTransactionHistory(User user) {
        List<Transaction> transactions = new ArrayList<>();
        List<BankAccount> accounts = bankAccountService.getAllMyAccounts(user);
        for (BankAccount account : accounts) {
            transactions.addAll(transactionRepository.findByBankAccount(account));
        }
        return transactions;
    }

    public List<Transaction> findByBankAccount(BankAccount bankAccount) {
        return transactionRepository.findByBankAccount(bankAccount);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(Long id) {
        transactionRepository.deleteById(id);
    }

    @Transactional
    public Transaction createTransaction(String accountNumber, TransactionType transactionType, BigDecimal amount) {
        Optional<BankAccount> account = bankAccountRepository.findByAccountNumber(accountNumber);
        if (account.isEmpty()) {
            throw new RuntimeException("Account not found");
        } else {
            BankAccount bankAccount = account.get();
            if (WITHDRAW.equals(transactionType) && bankAccount.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient funds");
            }

            if (WITHDRAW.equals(transactionType)) {
                bankAccount.setBalance(bankAccount.getBalance().subtract(amount));
            } else if (DEPOSIT.equals(transactionType)) {
                bankAccount.setBalance(bankAccount.getBalance().add(amount));
            }

            bankAccountRepository.save(bankAccount);

            Transaction transaction = new Transaction();
            transaction.setBankAccount(bankAccount);
            transaction.setTransactionType(transactionType);
            transaction.setAmount(amount);
            transaction.setTimestamp(LocalDateTime.now());

            Transaction savedTransaction = transactionRepository.save(transaction);

            // Send Email Notification
            String subject = "Transaction Alert: " + transactionType;
            String message = "Dear User,\n\n" +
                    "Your account " + bankAccount.getAccountNumber() + " has a new " + transactionType + " transaction.\n" +
                    "Amount: $" + amount + "\n" +
                    "Current Balance: $" + bankAccount.getBalance() + "\n\n" +
                    "Thank you for banking with us!";
            notificationService.sendTransactionEmail("user@example.com", subject, message);

            return savedTransaction;
        }
    }

    public List<Transaction> getTransactionsByAccount(String accountNumber) {
        Optional<BankAccount> account = bankAccountRepository.findByAccountNumber(accountNumber);
        if (account.isEmpty()) {
            throw new RuntimeException("Account not found");
        } else {
            return transactionRepository.findByBankAccount(account.get());
        }
    }
}
