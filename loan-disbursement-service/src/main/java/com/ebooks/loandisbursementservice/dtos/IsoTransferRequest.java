package com.ebooks.loandisbursementservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class IsoTransferRequest {
    private String bankCode;
    private String accountNumber;
    private BigDecimal amount;
    private String loanNumber;
}
