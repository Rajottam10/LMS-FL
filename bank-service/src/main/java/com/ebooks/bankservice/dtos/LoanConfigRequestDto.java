package com.ebooks.bankservice.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoanConfigRequestDto {
    @NotNull(message = "Min amount is required")
    private BigDecimal minAmount;

    @NotNull(message = "Max amount is required")
    private BigDecimal maxAmount;

    @NotNull(message = "Admin fee is required")
    private BigDecimal adminFee;

    @NotNull(message = "Fee type is required")
    private String feeType;
}