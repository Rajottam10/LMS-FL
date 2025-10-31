package com.ebooks.accrualsaccountingservice.services;

import com.ebooks.accrualsaccountingservice.entities.EMISchedule;
import com.ebooks.accrualsaccountingservice.entities.InterestInfo;

import java.time.LocalDate;

public interface InterestInfoService {
    void createAccrual(InterestInfo interestInfo);
    boolean existsByLoanAndInstallmentAndDate(String loanNumber, Long installmentNumber, LocalDate today);
}
