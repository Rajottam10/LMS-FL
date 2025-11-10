package com.ebooks.systemservice.controllers;

import com.ebooks.commonservice.dtos.LoginRequest;
import com.ebooks.commonservice.dtos.LoginResponse;
import com.ebooks.commonservice.services.JwtService;
import jakarta.validation.Valid;
import lombok.Generated;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/auth"})
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping({"/login"})
    public ResponseEntity<LoginResponse> loginUser(@RequestBody @Valid LoginRequest loginRequest) {
        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            Authentication authentication = this.authenticationManager.authenticate(token);
            if (authentication.isAuthenticated()) {
                String jwtToken = this.jwtService.generateToken(loginRequest.getUsername());
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + jwtToken);
                LoginResponse response = new LoginResponse();
                response.setMessage("Login successful");
                return ((ResponseEntity.BodyBuilder)ResponseEntity.ok().headers(headers)).body(response);
            } else {
                LoginResponse response = new LoginResponse();
                response.setMessage("Login failed: Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (AuthenticationException e) {
            LoginResponse response = new LoginResponse();
            response.setMessage("Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @Generated
    public AuthController(final AuthenticationManager authenticationManager, final JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }
}
