package com.ebooks.accrualsaccountingservice.services.impl;


import com.ebooks.accrualsaccountingservice.services.OverdueInfoService;
import com.ebooks.commonmoduleloan.entities.OverdueInfo;
import com.ebooks.commonmoduleloan.repositories.OverdueInfoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class OverdueInfoInfoServiceImpl implements OverdueInfoService {
    private final OverdueInfoRepository overdueInfoRepository;

    public OverdueInfoInfoServiceImpl(OverdueInfoRepository overdueInfoRepository){
        this.overdueInfoRepository = overdueInfoRepository;
    }

    @Override
    public void createOverdue(OverdueInfo overdueInfo) {
        overdueInfoRepository.save(overdueInfo);
    }

    @Override
    public boolean existsByLoanAndInstallmentAndDate(String loanNumber, Integer installmentNumber, LocalDate d) {
        return overdueInfoRepository.existsByLoanNumberAndInstallmentNumberAndAccrualDate(loanNumber, installmentNumber, d);
    }

}
