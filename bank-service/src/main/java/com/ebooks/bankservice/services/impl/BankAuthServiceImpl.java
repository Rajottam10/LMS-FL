package com.ebooks.bankservice.services.impl;

import com.ebooks.bankservice.dtos.BankAdminLoginRequestDto;
import com.ebooks.bankservice.dtos.BankAdminLoginResponseDto;
import com.ebooks.bankservice.entities.BankUser;
import com.ebooks.bankservice.repositories.BankAdminRepository;
import com.ebooks.bankservice.services.BankAuthService;
import com.ebooks.commonservice.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BankAuthServiceImpl implements BankAuthService {

    @Autowired
    private BankAdminRepository bankAdminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Override
    public BankAdminLoginResponseDto login(BankAdminLoginRequestDto loginRequest) {
        BankUser bankUser = bankAdminRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), bankUser.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        if (!"ACTIVE".equals(bankUser.getStatus())) {
            throw new RuntimeException("Account is not active");
        }

        if (!Boolean.TRUE.equals(bankUser.getIsAdmin())) {
            throw new RuntimeException("Access denied. Bank admin privileges required.");
        }

        BankAdminLoginResponseDto response = new BankAdminLoginResponseDto();
        response.setToken("temp-token-no-security");
        response.setMessage("Login successful (Security disabled)");
        response.setUsername(bankUser.getUsername());
        response.setBankId(bankUser.getBank().getId());
        response.setBankName(bankUser.getBank().getName());

        return response;
    }
}