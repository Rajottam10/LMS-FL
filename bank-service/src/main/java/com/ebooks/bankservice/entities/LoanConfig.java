package com.ebooks.bankservice.entities;

import com.ebooks.bankservice.enums.AdminFeeType;
import com.ebooks.commonservice.entities.Bank;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Generated;

@Entity
public class LoanConfig {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    private Double minAmount;
    private Double maxAmount;
    @Enumerated(EnumType.STRING)
    private AdminFeeType adminFeeType;
    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "bank_id",
            nullable = false
    )
    @JsonIgnore
    private Bank bank;

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public Double getMinAmount() {
        return this.minAmount;
    }

    @Generated
    public Double getMaxAmount() {
        return this.maxAmount;
    }

    @Generated
    public AdminFeeType getAdminFeeType() {
        return this.adminFeeType;
    }

    @Generated
    public Bank getBank() {
        return this.bank;
    }

    @Generated
    public void setId(final Long id) {
        this.id = id;
    }

    @Generated
    public void setMinAmount(final Double minAmount) {
        this.minAmount = minAmount;
    }

    @Generated
    public void setMaxAmount(final Double maxAmount) {
        this.maxAmount = maxAmount;
    }

    @Generated
    public void setAdminFeeType(final AdminFeeType adminFeeType) {
        this.adminFeeType = adminFeeType;
    }

    @Generated
    public void setBank(final Bank bank) {
        this.bank = bank;
    }
}
