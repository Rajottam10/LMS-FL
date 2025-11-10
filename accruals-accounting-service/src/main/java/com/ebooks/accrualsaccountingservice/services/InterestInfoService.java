package com.ebooks.accrualsaccountingservice.services;


import com.ebooks.commonmoduleloan.entities.InterestInfo;

import java.time.LocalDate;

public interface InterestInfoService {
    void createAccrual(InterestInfo interestInfo);
    boolean existsByLoanAndInstallmentAndDate(String loanNumber, Integer installmentNumber, LocalDate today);
}
