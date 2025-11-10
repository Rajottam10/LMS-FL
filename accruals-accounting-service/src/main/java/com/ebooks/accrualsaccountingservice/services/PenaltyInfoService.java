package com.ebooks.accrualsaccountingservice.services;


import com.ebooks.commonmoduleloan.entities.PenaltyInfo;

import java.time.LocalDate;

public interface PenaltyInfoService {
    void createPenalty(PenaltyInfo penaltyInfo);


    boolean existsByLoanAndInstallmentAndDate(String loanNumber, Integer installmentNumber, LocalDate d);
}
