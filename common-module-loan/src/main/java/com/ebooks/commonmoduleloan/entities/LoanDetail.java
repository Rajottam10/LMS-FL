package com.ebooks.commonmoduleloan.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loan_detail")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoanDetail {

    @Id
    @Column(name = "loan_number", length = 20)
    private String loanNumber;

    @Column(name = "customer_number", nullable = false, length = 20)
    private String customerNumber;

    @Column(name = "bank_code", nullable = false, length = 10)
    private String bankCode;

    @Column(name = "loan_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal loanAmount;

    @Column(name = "tenure", nullable = false)
    private Integer tenure;

    @Column(name = "loan_start_date", nullable = false)
    private LocalDate loanStartDate;

    @Column(name = "loan_end_date", nullable = false)
    private LocalDate loanEndDate;

    @Column(name = "loan_admin_fee", precision = 10, scale = 2)
    private BigDecimal loanAdminFee = BigDecimal.valueOf(150);

    @Column(name = "status", length = 20)
    private String status = "BOOKED";

    @Column(name = "paid_amount", precision = 15, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "annual_interest_rate", precision = 5, scale = 2)
    private BigDecimal annualInterestRate = BigDecimal.valueOf(12.00);

    @Column(name = "penalty_rate", precision = 5, scale = 2)
    private BigDecimal penaltyRate = BigDecimal.valueOf(2.00);

    @Column(name = "overdue_rate", precision = 5, scale = 2)
    private BigDecimal overdueRate = BigDecimal.valueOf(7.00);

    @Column(name = "late_fee", precision = 10, scale = 2)
    private BigDecimal lateFee = BigDecimal.valueOf(200.00);
}