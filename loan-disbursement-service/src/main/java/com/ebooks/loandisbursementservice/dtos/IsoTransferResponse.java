package com.ebooks.loandisbursementservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class IsoTransferResponse {
    private String transactionId;
    private String status;
    private String message;
    private BigDecimal amount;
    private String accountNumber;
    private String bankCode;
}
