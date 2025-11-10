package com.ebooks.accrualsaccountingservice.services.impl;


import com.ebooks.accrualsaccountingservice.services.InterestInfoService;
import com.ebooks.commonmoduleloan.entities.InterestInfo;
import com.ebooks.commonmoduleloan.repositories.EMIScheduleRepository;
import com.ebooks.commonmoduleloan.repositories.InterestInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class InterestInfoServiceImpl implements InterestInfoService {

    private final InterestInfoRepository interestInfoRepository;
    private final EMIScheduleRepository emiScheduleRepository;

    @Override
    public void createAccrual(InterestInfo interestInfo) {
        interestInfoRepository.save(interestInfo);
    }


    @Override
    public boolean existsByLoanAndInstallmentAndDate(String loanNumber, Integer installmentNumber, LocalDate accrualDate) {
        return interestInfoRepository.existsByLoanNumberAndInstallmentNumberAndAccrualDate(
                loanNumber,
                installmentNumber,
                accrualDate
        );
    }
}

