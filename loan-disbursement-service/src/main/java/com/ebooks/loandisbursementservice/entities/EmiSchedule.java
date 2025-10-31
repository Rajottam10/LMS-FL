package com.ebooks.loandisbursementservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "emi_schedule")
@Getter
@Setter
public class EmiSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_number", nullable = false)
    @NotNull(message = "Loan number is required")
    private String loanNumber;

    @Column(name = "installment_number", nullable = false)
    @NotNull(message = "Installment number is required")
    private Integer installmentNumber;

    @Column(name = "due_date", nullable = false)
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @Column(name = "principal_amount", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "Principal amount is required")
    private BigDecimal principalAmount;

    @Column(name = "interest_amount", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "Interest amount is required")
    private BigDecimal interestAmount;

    @Column(name = "total_emi", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "Total EMI is required")
    private BigDecimal totalEmi;

    @Column(name = "remaining_principal", precision = 15, scale = 2)
    private BigDecimal remainingPrincipal;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
        if (paymentStatus == null) {
            paymentStatus = "PENDING";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}