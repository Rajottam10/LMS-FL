package com.ebooks.accrualsaccountingservice.services.impl;

import com.ebooks.accrualsaccountingservice.entities.InterestInfo;
import com.ebooks.accrualsaccountingservice.repositories.EMIScheduleRepository;
import com.ebooks.accrualsaccountingservice.repositories.InterestInfoRepository;
import com.ebooks.accrualsaccountingservice.services.InterestInfoService;
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
    public boolean existsByLoanAndInstallmentAndDate(String loanNumber, Long installmentNumber, LocalDate accrualDate) {
        return interestInfoRepository.existsByLoanNumberAndInstallmentNumberAndAccrualDate(
                loanNumber,
                installmentNumber,
                accrualDate
        );
    }
}

