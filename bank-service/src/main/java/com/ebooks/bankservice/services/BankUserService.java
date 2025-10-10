package com.ebooks.bankservice.services;


import com.ebooks.bankservice.dtos.BankUserRequestDto;
import com.ebooks.bankservice.dtos.BankUserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BankUserService {
    BankUserResponseDto createBankUser(BankUserRequestDto requestDTO);
    List<BankUserResponseDto> getBankUsersByCurrentBank();
    BankUserResponseDto getBankUserById(Long userId);
    BankUserResponseDto updateBankUser(Long userId, BankUserRequestDto requestDTO);
    void deleteBankUser(Long userId);
    void blockBankUser(Long userId);
    void unblockBankUser(Long userId);
    Page<BankUserResponseDto> getBankUsersByCurrentBank(Pageable pageable);
}
