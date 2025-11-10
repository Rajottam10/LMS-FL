package com.ebooks.prepaymentservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "prepayment_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrepaymentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_number", nullable = false, length = 30)
    @NotBlank(message = "Loan number is mandatory")
    private String loanNumber;

    @Column(name = "bank_code", nullable = false, length = 10)
    @NotBlank(message = "Bank code is mandatory")
    private String bankCode;

    @Column(name = "prepay_date")
    @NotNull(message = "Prepayment date is mandatory")
    private LocalDate prepayDate;

    @Column(name = "principal",  precision = 15, scale = 2)
    @NotNull
    @DecimalMin("0.00")
    private BigDecimal principal;

    @Column(name = "accrued_interest", precision = 15, scale = 2)
    @NotNull
    @DecimalMin("0.00")
    private BigDecimal accruedInterest;

    @Column(name = "accrued_overdue_interest", precision = 15, scale = 2)
    @NotNull
    @DecimalMin("0.00")
    private BigDecimal accruedOverdueInterest;

    @Column(name = "accrued_penalty_interest", precision = 15, scale = 2)
    @NotNull
    @DecimalMin("0.00")
    private BigDecimal accruedPenaltyInterest;

    @Column(name = "accrued_late_fee",  precision = 15, scale = 2)
    @NotNull
    @DecimalMin("0.00")
    private BigDecimal accruedLateFee;

    @Column(name = "prepay_charge", precision = 15, scale = 2)
    @NotNull
    @DecimalMin("0.00")
    private BigDecimal prepayCharge;

    @Column(name = "total_paid", precision = 15, scale = 2)
    @DecimalMin("0.00")
    private BigDecimal totalPaid;

    @Column(name = "paid_principal", precision = 15, scale = 2)
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal paidPrincipal;

    @Column(name = "paid_interest", precision = 15, scale = 2)
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal paidInterest;

    @Column(name = "paid_overdue_interest", precision = 15, scale = 2)
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal paidOverdueInterest;

    @Column(name = "paid_penalty_interest", precision = 15, scale = 2)
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal paidPenaltyInterest;

    @Column(name = "paid_late_fee", precision = 15, scale = 2)
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal paidLateFee;

    @Column(name = "status", nullable = false, length = 20)
    @NotBlank
    private String status; // PENDING, SUCCESS, FAILED, REVERSED

    @Column(name = "transaction_id", length = 50)
    private String transactionId; // From Bank ISO

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}