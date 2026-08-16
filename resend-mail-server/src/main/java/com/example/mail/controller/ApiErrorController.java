package com.example.mail.controller;

import com.example.mail.dto.MailResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Replaces Spring Boot's HTML whitelabel error page so every error - including
 * ones raised outside a controller - comes back as the same JSON shape.
 */
@RestController
public class ApiErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<MailResponse> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus httpStatus = status == null
                ? HttpStatus.INTERNAL_SERVER_ERROR
                : HttpStatus.resolve(Integer.parseInt(status.toString()));
        if (httpStatus == null) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.status(httpStatus)
                .body(MailResponse.failure(httpStatus.getReasonPhrase()));
    }
}
