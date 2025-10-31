package com.ebooks.accrualsaccountingservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;


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
    private LocalDate loanBeginDate;
    private LocalDate loanEndDate;
    private BigDecimal loanAdminFee;
    private String status;
}

