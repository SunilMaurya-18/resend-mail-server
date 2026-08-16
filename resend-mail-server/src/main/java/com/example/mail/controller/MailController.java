package com.example.mail.controller;

import com.example.mail.dto.MailResponse;
import com.example.mail.dto.SendMailRequest;
import com.example.mail.service.MailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes the single application endpoint: POST /api/mail/send.
 */
@RestController
@RequestMapping("/api/mail")
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/send")
    public ResponseEntity<MailResponse> send(@Valid @RequestBody SendMailRequest request) {
        boolean sent = mailService.sendEmail(request.to(), request.subject(), request.content());

        if (sent) {
            return ResponseEntity.ok(MailResponse.ok());
        }

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(MailResponse.failure("Failed to send email"));
    }
}
