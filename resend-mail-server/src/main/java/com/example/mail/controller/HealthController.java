package com.example.mail.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Health check endpoints for platform probes. Both / and /health respond so
 * a default root-path check succeeds without hitting the 404 handler.
 */
@RestController
public class HealthController {

    @GetMapping({"/", "/health"})
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }
}
