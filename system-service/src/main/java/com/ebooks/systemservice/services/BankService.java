package com.ebooks.systemservice.services;


import com.ebooks.systemservice.dtos.bank.BankCreationRequestDto;
import com.ebooks.systemservice.dtos.bank.BankResponseDto;

import java.util.List;

public interface BankService {
    BankResponseDto createBank(BankCreationRequestDto request);
    BankResponseDto getBankByCode(String bankCode);
    List<BankResponseDto> getAllBanks();
    BankResponseDto updateBank(Long bankId, BankCreationRequestDto request);
    void deleteBank(Long bankId);
}
