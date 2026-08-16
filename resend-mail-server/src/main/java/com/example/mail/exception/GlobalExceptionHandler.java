package com.example.mail.exception;

import com.example.mail.dto.MailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Keeps error handling minimal: invalid request data becomes a 400 with a
 * safe message, anything unexpected becomes a 500 with a safe message.
 * Stack traces, secrets, and API keys are never returned to the client.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MailResponse> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(MailResponse.failure("Invalid request data"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<MailResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(MailResponse.failure("Invalid request data"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MailResponse> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MailResponse.failure("Internal server error"));
    }
}
