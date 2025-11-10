package com.ebooks.commonmoduleloan.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "emi_schedule",
        uniqueConstraints = @UniqueConstraint(columnNames = {"loan_number", "installment_number"})
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EMISchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_number", length = 20, nullable = false)
    private String loanNumber;

    @Column(name = "installment_number", nullable = false)
    private Integer installmentNumber;

    @Column(name = "installment_start_date", nullable = false)
    @NonNull
    private LocalDate installmentStartDate;

    @Column(name = "demand_date")
    private LocalDate demandDate;

    @Column(name = "emi_amount", precision = 15, scale = 2)
    private BigDecimal emiAmount;

    @Column(name = "principal", precision = 15, scale = 2)
    private BigDecimal principal;

    @Column(name = "interest", precision = 15, scale = 2)
    private BigDecimal interest;

    @Column(name = "interest_applicable_days")
    private Integer interestApplicableDays;

    @Column(name = "beginning_balance", precision = 15, scale = 2)
    private BigDecimal beginningBalance;

    @Column(name = "ending_balance", precision = 15, scale = 2)
    private BigDecimal endingBalance;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE";

    @Column(name = "paid_principal", precision = 15, scale = 2)
    private BigDecimal paidPrincipal = BigDecimal.ZERO;

    @Column(name = "paid_interest", precision = 15, scale = 2)
    private BigDecimal paidInterest = BigDecimal.ZERO;

    @Column(name = "paid_overdue_interest", precision = 15, scale = 2)
    private BigDecimal paidOverdueInterest = BigDecimal.ZERO;

    @Column(name = "paid_penalty_interest", precision = 15, scale = 2)
    private BigDecimal paidPenaltyInterest = BigDecimal.ZERO;

    @Column(name = "paid_late_fee", precision = 15, scale = 2)
    private BigDecimal paidLateFee = BigDecimal.ZERO;
}