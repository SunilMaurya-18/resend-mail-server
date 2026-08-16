package com.example.mail.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for POST /api/mail/send.
 */
public record SendMailRequest(

        @NotBlank(message = "secret is required")
        String secret,

        @NotBlank(message = "to is required")
        @Email(message = "to must be a valid email address")
        String to,

        @NotBlank(message = "subject is required")
        String subject,

        @NotBlank(message = "content is required")
        String content
) {
}
