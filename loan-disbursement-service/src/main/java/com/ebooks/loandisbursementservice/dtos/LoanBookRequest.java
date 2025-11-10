package com.ebooks.loandisbursementservice.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class LoanBookRequest {
    @NotBlank(message = "Customer number cannot be blank")
    private String customerNumber;
    private String bankCode;
    private BigDecimal loanAmount;
    private Integer tenure;
    private LocalDate emiStartDate;
}
