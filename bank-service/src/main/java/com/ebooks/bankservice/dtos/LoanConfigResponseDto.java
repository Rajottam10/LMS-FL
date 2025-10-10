package com.ebooks.bankservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class LoanConfigResponseDto {
    private Long id;
    private Long bankId;
    private String bankName;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal adminFee;
    private String feeType;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
