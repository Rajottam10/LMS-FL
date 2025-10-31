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
@Table(name = "emi_schedule")
@Getter
@Setter
@RequiredArgsConstructor
public class EMISchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long installmentNumber;
    private LocalDate installmentStartDate;
    private LocalDate demandDate;
    private BigDecimal beginningBalance;
    private BigDecimal eiAmount;
    private BigDecimal principal;
    private BigDecimal interestAmount;
    private BigDecimal endingBalance;
    private Integer interestApplicableDays;
    private String status;
    private String loanNumber;
}
