package com.example.banking.api;

import com.example.banking.service.StatementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.APPLICATION_PDF;

@RequestMapping("/api/statements")
@RestController
public class StatementController {
    private final StatementService statementService;

    public StatementController(StatementService statementService) {
        this.statementService = statementService;
    }

    @GetMapping("/generate/{accountNumber}")
    public ResponseEntity<byte[]> generateStatement(@PathVariable String accountNumber) {
        byte[] pdfData = statementService.generateStatement(accountNumber);

        return ResponseEntity.ok()
                .header(CONTENT_DISPOSITION, "attachment; filename=statement.pdf")
                .contentType(APPLICATION_PDF)
                .body(pdfData);
    }
}
