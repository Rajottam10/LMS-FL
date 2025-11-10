package com.ebooks.commonmoduleloan.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "emi_accounting_interest_info",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"loan_number", "installment_number", "accrual_date"}
        )
)
@Getter
@Setter
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class InterestInfo extends BaseAccountingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_number")
    private String loanNumber;

    @Column(name = "installment_number")
    private Integer installmentNumber;

    @Column(name = "daily_interest_amount")
    private BigDecimal dailyInterestAmount;

    @Column(name = "accrual_date")
    private LocalDate accrualDate;
}