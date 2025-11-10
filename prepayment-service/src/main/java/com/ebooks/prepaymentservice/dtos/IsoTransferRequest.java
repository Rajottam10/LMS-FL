package com.ebooks.prepaymentservice.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class IsoTransferRequest {
    private String bankCode;
    private String accountNumber;
    private String creditAccount;
    private String loanNumber;
    private BigDecimal amount;
    private String transferType;
}
