package com.ebooks.accrualsaccountingservice.services.impl;


import com.ebooks.accrualsaccountingservice.services.LateFeeInfoService;
import com.ebooks.commonmoduleloan.entities.LateFeeInfo;
import com.ebooks.commonmoduleloan.repositories.LateFeeInfoRepository;
import org.springframework.stereotype.Service;


@Service
public class LateFeeInfoServiceImpl implements LateFeeInfoService {
    private final LateFeeInfoRepository lateFeeInfoRepository;

    public LateFeeInfoServiceImpl(LateFeeInfoRepository lateFeeInfoRepository){
        this.lateFeeInfoRepository = lateFeeInfoRepository;
    }

    @Override
    public void createLateFee(LateFeeInfo lateFeeInfo){
        lateFeeInfoRepository.save(lateFeeInfo);
    }

    @Override
    public boolean existsByLoanAndInstallment(String loanNumber, Integer installmentNumber) {
        return lateFeeInfoRepository.existsByLoanNumberAndInstallmentNumber(loanNumber, installmentNumber);
    }

}
