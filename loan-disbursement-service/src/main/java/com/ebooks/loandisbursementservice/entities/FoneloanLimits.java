package com.ebooks.loandisbursementservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "foneloan_limits")
@Getter
@Setter
public class FoneloanLimits {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_number", nullable = false)
    @NotNull(message = "Customer number is required")
    private String customerNumber;

    @Column(name = "bank_code", nullable = false)
    @NotNull(message = "Bank code is required")
    private String bankCode;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "one_month_recommended_limit", precision = 15, scale = 2)
    private BigDecimal oneMonthRecommendedLimit;

    @Column(name = "emi_months", nullable = false)
    @NotNull(message = "EMI months is required")
    private Integer emiMonths;

    @Column(name = "emi_max_amount", precision = 15, scale = 2)
    private BigDecimal emiMaxAmount;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
