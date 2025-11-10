package com.ebooks.commonmoduleloan.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "emi_accounting_overdue_info")
@Getter
@Setter
@Builder
public class OverdueInfo extends BaseAccountingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String loanNumber;
    private Integer installmentNumber;
    private LocalDate accrualDate;
    @Column(name = "overdue_amount")
    private BigDecimal overdueAmount;


    public OverdueInfo(Long id, String loanNumber, Integer installmentNumber, LocalDate accrualDate, BigDecimal overdueAmount) {
        this.id = id;
        this.loanNumber = loanNumber;
        this.installmentNumber = installmentNumber;
        this.accrualDate = accrualDate;
        this.overdueAmount = overdueAmount;
    }

    public OverdueInfo() {

    }
}
