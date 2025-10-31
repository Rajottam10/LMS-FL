package com.ebooks.loandisbursementservice.services;

import com.ebooks.loandisbursementservice.entities.BankLoanConfig;
import com.ebooks.loandisbursementservice.repositories.BankLoanConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankLoanConfigService {
    private final BankLoanConfigRepository repository;

    public BankLoanConfig getLoanConfigForBank(String bankCode){
        return repository.findBankLoanConfigByBankCode(bankCode).orElseThrow(
                () -> new IllegalArgumentException("No loan configuration found for bank with bankCode " + bankCode)
        );
    }
}
