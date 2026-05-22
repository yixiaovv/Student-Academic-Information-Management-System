package com.example.yxwbackend.controller;

import com.example.yxwbackend.service.ScholarshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/scholarship")
public class ScholarshipController {

    private final ScholarshipService scholarshipService;

    public ScholarshipController(ScholarshipService scholarshipService) {
        this.scholarshipService = scholarshipService;
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyScholarship(Authentication authentication) {
        try {
            return ResponseEntity.ok(scholarshipService.getMyScholarship(authentication.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/calculate/{studentId}")
    public ResponseEntity<?> calculate(@PathVariable Integer studentId) {
        try {
            return ResponseEntity.ok(scholarshipService.calculateForStudent(studentId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
