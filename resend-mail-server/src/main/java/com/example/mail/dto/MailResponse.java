package com.example.mail.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Response body for POST /api/mail/send.
 * The {@code error} field is omitted from the JSON output on success.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MailResponse(boolean success, String error) {

    public static MailResponse ok() {
        return new MailResponse(true, null);
    }

    public static MailResponse failure(String error) {
        return new MailResponse(false, error);
    }
}
