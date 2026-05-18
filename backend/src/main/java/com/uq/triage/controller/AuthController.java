package com.uq.triage.controller;

import com.uq.triage.dto.LoginRequestDTO;
import com.uq.triage.dto.LoginResponseDTO;
import com.uq.triage.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @GetMapping("/hash")
    public String hash(@RequestParam String pwd) {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(pwd);
    }
}
