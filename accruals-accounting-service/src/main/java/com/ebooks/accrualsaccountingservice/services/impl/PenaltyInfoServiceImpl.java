package com.ebooks.accrualsaccountingservice.services.impl;


import com.ebooks.accrualsaccountingservice.services.PenaltyInfoService;
import com.ebooks.commonmoduleloan.entities.PenaltyInfo;
import com.ebooks.commonmoduleloan.repositories.PenaltyInfoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PenaltyInfoServiceImpl implements PenaltyInfoService {
    private final PenaltyInfoRepository penaltyInfoRepository;

    public PenaltyInfoServiceImpl(PenaltyInfoRepository penaltyInfoRepository){
        this.penaltyInfoRepository = penaltyInfoRepository;
    }
    @Override
    public void createPenalty(PenaltyInfo penaltyInfo){
        penaltyInfoRepository.save(penaltyInfo);
    }

    @Override
    public boolean existsByLoanAndInstallmentAndDate(String loanNumber, Integer installmentNumber, LocalDate d) {
        return penaltyInfoRepository.existsByLoanNumberAndInstallmentNumberAndAccrualDate(loanNumber, installmentNumber, d);
    }
}
