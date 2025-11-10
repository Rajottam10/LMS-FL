package com.ebooks.bankisoservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IsoTransferResponse {
    private String transactionId;
    private String status;
    private String message;
    private BigDecimal amount;
    private String accountNumber;
    private String bankCode;
}
