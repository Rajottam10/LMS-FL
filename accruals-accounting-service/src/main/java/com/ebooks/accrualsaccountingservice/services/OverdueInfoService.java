package com.ebooks.accrualsaccountingservice.services;


import com.ebooks.commonmoduleloan.entities.OverdueInfo;

import java.time.LocalDate;


public interface OverdueInfoService {
    void createOverdue(OverdueInfo overdueInfo);

    boolean existsByLoanAndInstallmentAndDate(String loanNumber, Integer installmentNumber, LocalDate d);
}
