package com.ebooks.accrualsaccountingservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccrualReportResponse {
    private String loanNumber;
    private BigDecimal totalInterest = BigDecimal.ZERO;
    private BigDecimal totalPenaltyInterest = BigDecimal.ZERO;
    private BigDecimal totalOverdueInterest = BigDecimal.ZERO;
    private BigDecimal totalLateFee = BigDecimal.ZERO;

    public AccrualReportResponse(String loanNumber) {
        this.loanNumber = loanNumber;
    }
}
