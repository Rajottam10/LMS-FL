package com.ebooks.prepaymentservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PrepaymentConfirmRequest {
    private String loanNumber;
    private BigDecimal customerAmount;
}
