package com.example.banking.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;

@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@Service
public class NotificationService {

    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmailWithAttachment(String to, byte[] attachmentData) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("Your Monthly Bank Statement");
        helper.setText("Dear Customer,\n\nPlease find attached your monthly bank statement.\n\nBest Regards,\nBank");

        helper.addAttachment("Monthly_Statement.pdf", () -> new ByteArrayInputStream(attachmentData));

        mailSender.send(message);
    }

    public void sendTransactionEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendFraudAlert(String email, BigDecimal amount) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("🚨 Fraud Alert: Suspicious Transaction Detected");
        message.setText("A transaction of $" + amount + " was flagged as suspicious.\n" +
                "Please review your account immediately.");
        mailSender.send(message);
    }
}
