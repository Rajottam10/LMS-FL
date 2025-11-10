package com.ebooks.bankservice.dtos;

import com.ebooks.bankservice.enums.AdminFeeType;
import jakarta.validation.constraints.NotNull;
import lombok.Generated;

public class LoanConfigRequest {
    private @NotNull Double minAmount;
    private @NotNull Double maxAmount;
    private AdminFeeType adminFeeType;

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
}
