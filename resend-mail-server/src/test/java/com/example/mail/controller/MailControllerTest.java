package com.example.mail.controller;

import com.example.mail.service.MailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-layer tests for MailController.
 * <p>
 * MailService is mocked, so these tests never need a real RESEND_API_KEY or
 * MAIL_FROM value and never make a real network call.
 */
@WebMvcTest(MailController.class)
class MailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MailService mailService;

    @Test
    void validRequestReachesMailService() throws Exception {
        when(mailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);

        Map<String, String> requestBody = Map.of(
                "to", "receiver@example.com",
                "subject", "Test email",
                "content", "<h1>Hello</h1><p>This is a test email.</p>"
        );

        mockMvc.perform(post("/api/mail/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(mailService, times(1))
                .sendEmail("receiver@example.com", "Test email", "<h1>Hello</h1><p>This is a test email.</p>");
    }

    @Test
    void invalidRequestDataReturns400() throws Exception {
        // Missing "to", blank "subject", missing "content"
        Map<String, String> requestBody = Map.of(
                "subject", ""
        );

        mockMvc.perform(post("/api/mail/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
