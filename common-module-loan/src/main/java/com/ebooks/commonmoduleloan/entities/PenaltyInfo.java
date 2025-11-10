package com.ebooks.commonmoduleloan.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "emi_accounting_penalty_info")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class PenaltyInfo extends BaseAccountingEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String loanNumber;
    private Integer installmentNumber;
    private LocalDate accrualDate;
    @Column(name = "penalty_amount")
    private BigDecimal penaltyAmount;

    public PenaltyInfo() {

    }
}
