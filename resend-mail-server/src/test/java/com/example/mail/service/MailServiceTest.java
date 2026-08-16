package com.example.mail.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Plain unit test for the secret-comparison logic. Instantiates MailService
 * directly with fake configuration values - no Spring context, no real
 * Resend API key required.
 */
class MailServiceTest {

    private final MailService mailService = new MailService(
            new RestTemplateBuilder(),
            "fake-resend-api-key",
            "sender@example.com",
            "correct-secret"
    );

    @Test
    void validSecretIsAccepted() {
        assertTrue(mailService.isSecretValid("correct-secret"));
    }

    @Test
    void invalidSecretIsRejected() {
        assertFalse(mailService.isSecretValid("wrong-secret"));
    }

    @Test
    void nullSecretIsRejected() {
        assertFalse(mailService.isSecretValid(null));
    }
}
