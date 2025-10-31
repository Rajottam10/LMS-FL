package com.ebooks.loandisbursementservice.entities;

import com.ebooks.loandisbursementservice.enums.AdminFeeType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bank_loan_config")
@SQLDelete(sql = "UPDATE bank_loan_config SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class BankLoanConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bankCode;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    @Enumerated(EnumType.STRING)
    private AdminFeeType adminFeeType;
    private BigDecimal adminFeeValue;
    private BigDecimal annualInterestRate;
    private boolean deleted = Boolean.FALSE;
}