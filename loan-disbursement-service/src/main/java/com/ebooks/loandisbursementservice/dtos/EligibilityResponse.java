package com.ebooks.loandisbursementservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EligibilityResponse {
    private String customerNumber;
    private String customerName;
    private String bankCode;
    private double oneMonthRecommendedLimit;
    private int emiMonths;
    private double emiMaxAmount;
}
