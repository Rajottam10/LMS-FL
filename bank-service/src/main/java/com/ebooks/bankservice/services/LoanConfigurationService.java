package com.ebooks.bankservice.services;

import com.ebooks.bankservice.dtos.LoanConfigRequestDto;
import com.ebooks.bankservice.dtos.LoanConfigResponseDto;

import java.util.List;

public interface LoanConfigurationService {
    LoanConfigResponseDto createLoanConfiguration(LoanConfigRequestDto requestDto);
    LoanConfigResponseDto getCurrentBankLoanConfiguration();
    List<LoanConfigResponseDto> getBankLoanConfigurations();
    LoanConfigResponseDto updateLoanConfiguration(Long configId, LoanConfigRequestDto requestDto);
    void deactivateLoanConfiguration(Long configId);
    void activateLoanConfiguration(Long configId);
}