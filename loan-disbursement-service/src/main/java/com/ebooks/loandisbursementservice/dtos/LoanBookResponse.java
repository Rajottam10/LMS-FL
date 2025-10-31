package com.ebooks.loandisbursementservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class LoanBookResponse {
    private List<LoanSchedule> schedules;
    private BigDecimal loanAdminFee;
}
