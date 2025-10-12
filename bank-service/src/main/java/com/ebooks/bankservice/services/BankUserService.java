package com.ebooks.bankservice.services;

import com.ebooks.bankservice.dtos.BankUserRequestDto;
import com.ebooks.bankservice.dtos.BankUserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BankUserService {
    BankUserResponseDto createBankUser(BankUserRequestDto request);
    BankUserResponseDto getBankUserById(Long id);

    // Add these missing methods:
    List<BankUserResponseDto> getBankUsersByCurrentBank();
    Page<BankUserResponseDto> getBankUsersByCurrentBank(Pageable pageable);

    BankUserResponseDto updateBankUser(Long id, BankUserRequestDto request);
    void deleteBankUser(Long id);
    void blockBankUser(Long id);  // Changed from BankUserResponseDto to void
    void unblockBankUser(Long id); // Changed from BankUserResponseDto to void
}