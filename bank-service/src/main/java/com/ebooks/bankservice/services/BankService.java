package com.ebooks.bankservice.services;

import com.ebooks.bankservice.dtos.BankCreationRequestDto;
import com.ebooks.bankservice.dtos.BankResponseDto;

import java.util.List;

public interface BankService {
    BankResponseDto createBank(BankCreationRequestDto request);
    BankResponseDto getBankByCode(String bankCode);
    List<BankResponseDto> getAllBanks();
    BankResponseDto updateBank(Long bankId, BankCreationRequestDto request);
    void deleteBank(Long bankId);
}
