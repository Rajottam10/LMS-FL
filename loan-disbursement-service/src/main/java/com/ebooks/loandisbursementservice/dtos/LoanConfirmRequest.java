package com.ebooks.loandisbursementservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoanConfirmRequest {
    private String customerNumber;
    private String bankCode;
    private String otp;
}
