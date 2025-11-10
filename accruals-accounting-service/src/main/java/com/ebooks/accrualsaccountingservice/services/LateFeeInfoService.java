package com.ebooks.accrualsaccountingservice.services;


import com.ebooks.commonmoduleloan.entities.LateFeeInfo;

public interface LateFeeInfoService {
    void createLateFee(LateFeeInfo lateFeeInfo);
    boolean existsByLoanAndInstallment(String loanNumber, Integer installmentNumber);

}
