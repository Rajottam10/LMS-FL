package com.ebooks.systemservice.security;

import com.ebooks.commonservice.security.JwtUtil;
import com.ebooks.systemservice.dtos.login.LoginRequest;
import com.ebooks.systemservice.dtos.login.LoginResponse;
import com.ebooks.systemservice.entities.SystemUser;
import com.ebooks.systemservice.repositories.SystemUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    @Autowired
    private SystemUserRepository userRepository;

    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            SystemUser user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String jwt = jwtTokenGenerator.generateToken(user);

            List<String> roles = jwtUtil.getRolesFromToken(jwt);

            return new LoginResponse(
                    true,
                    "Login successful",
                    jwt,
                    user.getName(),
                    user.getEmail()
            );

        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid username or password");
        }
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
}