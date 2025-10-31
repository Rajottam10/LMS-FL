package com.ebooks.accrualsaccountingservice.entities;

import jakarta.persistence.*;
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
public class InterestInfo extends BaseAccountingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_number")
    private String loanNumber;

    @Column(name = "installment_number")
    private Long installmentNumber;

    @Column(name = "daily_interest_amount")
    private BigDecimal dailyInterestAmount;

    @Column(name = "accrual_date")
    private LocalDate accrualDate;
}