package com.example.mail.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Sends email through the Resend API.
 * <p>
 * Contains no user system, no database, no JWT/OAuth - this service does
 * exactly one thing: call Resend.
 */
@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private static final String RESEND_URL = "https://api.resend.com/emails";

    private final RestTemplate restTemplate;
    private final String resendApiKey;
    private final String mailFrom;

    public MailService(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${resend.api-key}") String resendApiKey,
            @Value("${mail.from}") String mailFrom) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
        this.resendApiKey = resendApiKey;
        this.mailFrom = mailFrom;
    }

    /**
     * Sends an HTML email through Resend.
     *
     * @return true if Resend accepted the email (2xx response), false otherwise
     */
    public boolean sendEmail(String to, String subject, String htmlContent) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(resendApiKey);

        Map<String, Object> payload = Map.of(
                "from", mailFrom,
                "to", List.of(to),
                "subject", subject,
                "html", htmlContent
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(RESEND_URL, entity, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException ex) {
            // Never log the API key or the raw request body - just enough to debug.
            log.error("Resend request failed: {}", ex.getMessage());
            return false;
        }
    }