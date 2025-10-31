package com.ebooks.loandisbursementservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class LoanConfirmResponse {
    private String loanNumber;
    private BigDecimal loanAmount;
    private int tenure;
    private LocalDate paymentDate;
    private List<LoanSchedule> loanSchedules;
    private BigDecimal loanAdminFee;
    private String message;
    private String status;
    private String accountNumber;
}
