package com.nexus.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Bem-vindo à API MindTrack / Nexus");
        response.put("version", "1.0.0");
        response.put("documentation", "http://localhost:8080/swagger-ui.html");
        response.put("endpoints", Map.of(
            "swagger", "/swagger-ui.html",
            "api-docs", "/v3/api-docs",
            "auth", "/api/auth/login"
        ));
        return ResponseEntity.ok(response);
    }
}





