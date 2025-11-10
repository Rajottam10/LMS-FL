package com.ebooks.prepaymentservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PayableDetails {
    private BigDecimal principal;
    private BigDecimal interest;
    private BigDecimal lateFee = BigDecimal.ZERO;
    private BigDecimal overdueInterest = BigDecimal.ZERO;
    private BigDecimal penaltyInterest = BigDecimal.ZERO;
    private BigDecimal prePayCharge = BigDecimal.ZERO;
    private BigDecimal total;
}
