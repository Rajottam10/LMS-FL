package com.ebooks.accrualsaccountingservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "emi_accounting_overdue_info")
@Getter
@Setter
public class OverdueInfo extends BaseAccountingEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String loanNumber;
    private Long installmentNumber;
    private LocalDate accrualDate;
    @Column(name = "overdue_amount")
    private BigDecimal OverdueAmount;
}
