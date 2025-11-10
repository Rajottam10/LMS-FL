package com.ebooks.bankservice.services;

import com.ebooks.bankservice.dtos.LoanConfigRequest;
import com.ebooks.bankservice.entities.LoanConfig;
import com.ebooks.bankservice.mapper.LoanConfigMapper;
import com.ebooks.bankservice.repositories.LoanConfigRepository;
import com.ebooks.commonservice.entities.Bank;
import java.util.List;
import java.util.Optional;
import lombok.Generated;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanConfigService {
    private final LoanConfigRepository loanConfigRepository;
    private final LoanConfigMapper loanConfigMapper;

    @Transactional
    public LoanConfig createLoanConfig(LoanConfigRequest request, Bank bank) {
        LoanConfig loanConfig = this.loanConfigMapper.toEntity(request);
        loanConfig.setBank(bank);
        return (LoanConfig)this.loanConfigRepository.save(loanConfig);
    }

    @Transactional(
            readOnly = true
    )
    public List<LoanConfig> getAllLoanConfigs(Bank bank) {
        return this.loanConfigRepository.findLoanConfigByBank(bank);
    }

    @Transactional
    public void deleteLoanConfig(Long loanConfigId, Bank bank) {
        Optional<LoanConfig> optionalLoanConfig = this.loanConfigRepository.findById(loanConfigId);
        if (optionalLoanConfig.isEmpty()) {
            throw new RuntimeException("No config exists with the id " + loanConfigId);
        } else {
            LoanConfig loanConfig = (LoanConfig)optionalLoanConfig.get();
            if (!loanConfig.getBank().getId().equals(bank.getId())) {
                throw new RuntimeException("You are not allowed to access this resource.");
            } else {
                this.loanConfigRepository.delete(loanConfig);
            }
        }
    }

    @Transactional
    public LoanConfig updateLoanConfig(Long loanConfigId, LoanConfigRequest request, Bank bank) {
        Optional<LoanConfig> optionalLoanConfig = this.loanConfigRepository.findById(loanConfigId);
        if (optionalLoanConfig.isEmpty()) {
            throw new RuntimeException("No config exists with the id " + loanConfigId);
        } else {
            LoanConfig loanConfig = (LoanConfig)optionalLoanConfig.get();
            if (!loanConfig.getBank().getId().equals(bank.getId())) {
                throw new RuntimeException("You are not allowed to access this resource.");
            } else {
                loanConfig.setMinAmount(request.getMinAmount());
                loanConfig.setMaxAmount(request.getMaxAmount());
                loanConfig.setAdminFeeType(request.getAdminFeeType());
                return (LoanConfig)this.loanConfigRepository.save(loanConfig);
            }
        }
    }

    @Generated
    public LoanConfigService(final LoanConfigRepository loanConfigRepository, final LoanConfigMapper loanConfigMapper) {
        this.loanConfigRepository = loanConfigRepository;
        this.loanConfigMapper = loanConfigMapper;
    }
}
