package com.ebooks.loandisbursementservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class LoanProcessResponse {
    private BigDecimal eligibleLoanAmount;
    private List<Integer> tenures;
}
