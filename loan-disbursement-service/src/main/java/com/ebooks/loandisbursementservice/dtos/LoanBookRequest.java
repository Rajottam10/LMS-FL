package com.ebooks.loandisbursementservice.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class LoanBookRequest {
    @NotBlank(message = "b")
    private String customerNumber;
    private String bankCode;
    private BigDecimal loanAmount;
    private Integer tenure;
    private LocalDate paymentDate;
}
