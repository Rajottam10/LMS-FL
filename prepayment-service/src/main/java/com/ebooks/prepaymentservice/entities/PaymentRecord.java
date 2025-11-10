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

@Entity
@Table(name = "payment_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_number", nullable = false, length = 30)
    @NotBlank
    private String loanNumber;

    @Column(name = "emi_id")
    private Long emiId; // Nullable for full prepayment

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @Column(name = "paid_principal", nullable = false, precision = 15, scale = 2)
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal paidPrincipal;

    @Column(name = "paid_interest", nullable = false, precision = 15, scale = 2)
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal paidInterest;

    @Column(name = "paid_overdue_interest", nullable = false, precision = 15, scale = 2)
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal paidOverdueInterest;

    @Column(name = "paid_penalty_interest", nullable = false, precision = 15, scale = 2)
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal paidPenaltyInterest;

    @Column(name = "paid_late_fee", nullable = false, precision = 15, scale = 2)
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal paidLateFee;


    @Column(name = "type", nullable = false, length = 30)
    @NotBlank
    private String type; // LATE_FEE, PENALTY_INTEREST, OVERDUE_INTEREST, INTEREST, PRINCIPAL

    @Column(name = "payment_date", nullable = false)
    @NotNull
    private LocalDate paymentDate;

    @Column(name = "transaction_id", length = 50)
    private String transactionId;

    @Column(name = "prepayment_log_id")
    private Long prepaymentLogId; // FK reference

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt = LocalDate.now();
}