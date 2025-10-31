package com.ebooks.bankisoservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_balance")
@Getter
@Setter
public class AccountBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true)
    @NotNull(message = "Account number is required")
    private String accountNumber;

    @Column(name = "account_holder_name", nullable = false)
    @NotNull(message = "Account holder name is required")
    private String accountHolderName;

    @Column(name = "account_type", nullable = false)
    @NotNull(message = "Account type is required")
    private String accountType; // BANK, CUSTOMER

    @Column(name = "bank_code")
    private String bankCode; // Only for customer accounts

    @Column(name = "current_balance", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "Current balance is required")
    private BigDecimal currentBalance;

    @Column(name = "currency", nullable = false)
    private String currency = "NPR";

    @Column(name = "status", nullable = false)
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, BLOCKED

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
        if (currentBalance == null) {
            currentBalance = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
