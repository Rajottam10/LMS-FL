package com.ebooks.bankservice.services.impl;

import com.ebooks.bankservice.dtos.LoanConfigRequestDto;
import com.ebooks.bankservice.dtos.LoanConfigResponseDto;
import com.ebooks.bankservice.entities.Bank;
import com.ebooks.bankservice.entities.BankUser;
import com.ebooks.bankservice.entities.LoanConfiguration;
import com.ebooks.bankservice.repositories.LoanConfigurationRepository;
import com.ebooks.bankservice.services.BankAuthorizationService;
import com.ebooks.bankservice.services.LoanConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanConfigurationServiceImpl implements LoanConfigurationService {

    @Autowired
    private LoanConfigurationRepository loanConfigurationRepository;

    @Autowired
    private BankAuthorizationService bankAuthorizationService;

    @Override
    @Transactional
    public LoanConfigResponseDto createLoanConfiguration(LoanConfigRequestDto requestDto) {
        BankUser currentUser = bankAuthorizationService.getCurrentBankAdmin();
        Bank bank = currentUser.getBank();

        if (requestDto.getMinAmount().compareTo(requestDto.getMaxAmount()) >= 0) {
            throw new RuntimeException("Min amount must be less than max amount");
        }

        if (!isValidFeeType(requestDto.getFeeType())) {
            throw new RuntimeException("Invalid fee type. Must be FLAT, PERCENTAGE, or GREATER");
        }

        if (loanConfigurationRepository.existsByBankIdAndIsActiveTrue(bank.getId())) {
            throw new RuntimeException("Active loan configuration already exists for this bank");
        }

        LoanConfiguration loanConfig = new LoanConfiguration();
        loanConfig.setBank(bank);
        loanConfig.setMinAmount(requestDto.getMinAmount());
        loanConfig.setMaxAmount(requestDto.getMaxAmount());
        loanConfig.setAdminFee(requestDto.getAdminFee());
        loanConfig.setFeeType(requestDto.getFeeType());
        loanConfig.setIsActive(true);

        LoanConfiguration savedConfig = loanConfigurationRepository.save(loanConfig);
        return convertToDto(savedConfig);
    }

    @Override
    public LoanConfigResponseDto getCurrentBankLoanConfiguration() {
        BankUser currentUser = bankAuthorizationService.getCurrentBankAdmin();
        Long bankId = currentUser.getBank().getId();

        LoanConfiguration loanConfig = loanConfigurationRepository.findByBankIdAndIsActiveTrue(bankId)
                .orElseThrow(() -> new RuntimeException("No active loan configuration found for this bank"));

        return convertToDto(loanConfig);
    }

    @Override
    public List<LoanConfigResponseDto> getBankLoanConfigurations() {
        BankUser currentUser = bankAuthorizationService.getCurrentBankAdmin();
        Long bankId = currentUser.getBank().getId();

        List<LoanConfiguration> configs = loanConfigurationRepository.findByBankId(bankId);
        return configs.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LoanConfigResponseDto updateLoanConfiguration(Long configId, LoanConfigRequestDto requestDto) {
        BankUser currentUser = bankAuthorizationService.getCurrentBankAdmin();
        Long bankId = currentUser.getBank().getId();

        LoanConfiguration loanConfig = loanConfigurationRepository.findById(configId)
                .orElseThrow(() -> new RuntimeException("Loan configuration not found with id: " + configId));

        if (!loanConfig.getBank().getId().equals(bankId)) {
            throw new RuntimeException("Not authorized to update this loan configuration");
        }

        if (requestDto.getMinAmount().compareTo(requestDto.getMaxAmount()) >= 0) {
            throw new RuntimeException("Min amount must be less than max amount");
        }

        if (!isValidFeeType(requestDto.getFeeType())) {
            throw new RuntimeException("Invalid fee type. Must be FLAT, PERCENTAGE, or GREATER");
        }

        loanConfig.setMinAmount(requestDto.getMinAmount());
        loanConfig.setMaxAmount(requestDto.getMaxAmount());
        loanConfig.setAdminFee(requestDto.getAdminFee());
        loanConfig.setFeeType(requestDto.getFeeType());

        LoanConfiguration updatedConfig = loanConfigurationRepository.save(loanConfig);
        return convertToDto(updatedConfig);
    }

    @Override
    @Transactional
    public void deactivateLoanConfiguration(Long configId) {
        BankUser currentUser = bankAuthorizationService.getCurrentBankAdmin();
        Long bankId = currentUser.getBank().getId();

        LoanConfiguration loanConfig = loanConfigurationRepository.findById(configId)
                .orElseThrow(() -> new RuntimeException("Loan configuration not found with id: " + configId));

        if (!loanConfig.getBank().getId().equals(bankId)) {
            throw new RuntimeException("Not authorized to deactivate this loan configuration");
        }

        loanConfig.setIsActive(false);
        loanConfigurationRepository.save(loanConfig);
    }

    @Override
    @Transactional
    public void activateLoanConfiguration(Long configId) {
        BankUser currentUser = bankAuthorizationService.getCurrentBankAdmin();
        Long bankId = currentUser.getBank().getId();

        LoanConfiguration loanConfig = loanConfigurationRepository.findById(configId)
                .orElseThrow(() -> new RuntimeException("Loan configuration not found with id: " + configId));

        if (!loanConfig.getBank().getId().equals(bankId)) {
            throw new RuntimeException("Not authorized to activate this loan configuration");
        }

        loanConfigurationRepository.findByBankIdAndIsActiveTrue(bankId)
                .ifPresent(activeConfig -> {
                    activeConfig.setIsActive(false);
                    loanConfigurationRepository.save(activeConfig);
                });

        loanConfig.setIsActive(true);
        loanConfigurationRepository.save(loanConfig);
    }

    private boolean isValidFeeType(String feeType) {
        return feeType != null &&
                (feeType.equals("FLAT") || feeType.equals("PERCENTAGE") || feeType.equals("GREATER"));
    }

    private LoanConfigResponseDto convertToDto(LoanConfiguration loanConfig) {
        LoanConfigResponseDto dto = new LoanConfigResponseDto();
        dto.setId(loanConfig.getId());
        dto.setBankId(loanConfig.getBank().getId());
        dto.setBankName(loanConfig.getBank().getName());
        dto.setMinAmount(loanConfig.getMinAmount());
        dto.setMaxAmount(loanConfig.getMaxAmount());
        dto.setAdminFee(loanConfig.getAdminFee());
        dto.setFeeType(loanConfig.getFeeType());
        dto.setIsActive(loanConfig.getIsActive());
        dto.setCreatedAt(loanConfig.getCreatedAt());
        dto.setUpdatedAt(loanConfig.getUpdatedAt());
        return dto;
    }
}