package com.ebooks.loandisbursementservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "loan_detail")
public class LoanDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loanNumber;
    private String customerNumber;
    private String bankCode;
    private BigDecimal loanAmount;
    private Integer tenure;
    private LocalDate paymentDate;
    private BigDecimal loanAdminFee;
}