package com.ebooks.prepaymentservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoanDetails {
    private BigDecimal remainingAmount;
    private String processOnDate;
    private BigDecimal loanAdminFee;
    private BigDecimal paidAmount=BigDecimal.ZERO;
    private BigDecimal loanAmount;
}
