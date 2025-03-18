package com.example.banking.service;

import com.example.banking.model.BankAccount;
import com.example.banking.model.Transaction;
import com.example.banking.repository.BankAccountRepository;
import com.example.banking.repository.TransactionRepository;
import jakarta.mail.MessagingException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@Transactional(readOnly = true)
@Service
public class StatementService {
    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final NotificationService notificationService;

    public StatementService(TransactionRepository transactionRepository, BankAccountRepository bankAccountRepository, NotificationService notificationService) {
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.notificationService = notificationService;
    }

    public void sendMonthlyStatement(String accountNumber, String email) throws MessagingException {
        Optional<BankAccount> account = bankAccountRepository.findByAccountNumber(accountNumber);
        account.ifPresentOrElse(
                acc -> {
                    List<Transaction> transactions = transactionRepository.findByBankAccount(acc);

                    try (PDDocument document = new PDDocument();
                         ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                        PDPage page = new PDPage();
                        document.addPage(page);

                        PDPageContentStream contentStream = new PDPageContentStream(document, page);
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, 750);
                        contentStream.showText("Monthly Statement - " + LocalDate.now().getMonth());
                        contentStream.newLineAtOffset(0, -20);
                        contentStream.showText("Account Number: " + acc.getAccountNumber());
                        contentStream.newLineAtOffset(0, -20);
                        contentStream.showText("Balance: $" + acc.getBalance());
                        contentStream.newLineAtOffset(0, -40);

                        contentStream.setFont(PDType1Font.HELVETICA, 12);
                        contentStream.showText("Transactions:");
                        contentStream.newLineAtOffset(0, -20);

                        for (Transaction transaction : transactions) {
                            contentStream.showText(transaction.getTimestamp() + " - " +
                                    transaction.getTransactionType() + " - $" + transaction.getAmount());
                            contentStream.newLineAtOffset(0, -15);
                        }

                        contentStream.endText();
                        contentStream.close();
                        document.save(outputStream);

                        notificationService.sendEmailWithAttachment(email, outputStream.toByteArray());

                    } catch (Exception e) {
                        throw new RuntimeException("Error generating statement", e);
                    }
                }
                , () -> {
                    ;
                    throw new RuntimeException("Account not found");
                });
    }

    public byte[] generateStatement(String accountNumber) {
        var account = bankAccountRepository.findByAccountNumber(accountNumber);
        if (account.isEmpty()) {
            throw new RuntimeException("Account not found");
        } else {
            var bankAccount = account.get();
            List<Transaction> transactions = transactionRepository.findByBankAccount(bankAccount);

            try (PDDocument document = new PDDocument();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                PDPage page = new PDPage();
                document.addPage(page);

                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Bank Account Statement");
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Account Number: " + bankAccount.getAccountNumber());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Balance: $" + bankAccount.getBalance());
                contentStream.newLineAtOffset(0, -40);

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.showText("Transactions:");
                contentStream.newLineAtOffset(0, -20);

                for (Transaction transaction : transactions) {
                    contentStream.showText(transaction.getTimestamp() + " - " +
                            transaction.getTransactionType() + " - $" + transaction.getAmount());
                    contentStream.newLineAtOffset(0, -15);
                }

                contentStream.endText();
                contentStream.close();

                document.save(outputStream);
                return outputStream.toByteArray();
            } catch (Exception e) {
                throw new RuntimeException("Error generating statement", e);
            }
        }
    }
}
