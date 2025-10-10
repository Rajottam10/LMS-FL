package com.ebooks.bankservice.services;


import com.ebooks.bankservice.dtos.BankAdminLoginRequestDto;
import com.ebooks.bankservice.dtos.BankAdminLoginResponseDto;

public interface BankAuthService {
    BankAdminLoginResponseDto login(BankAdminLoginRequestDto loginRequest);
}