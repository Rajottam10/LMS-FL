package com.ebooks.systemservice.controllers;

import com.ebooks.systemservice.dtos.login.LoginRequest;
import com.ebooks.systemservice.dtos.login.LoginResponse;
import com.ebooks.systemservice.security.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + response.getToken())
                .body(response);
    }
}
