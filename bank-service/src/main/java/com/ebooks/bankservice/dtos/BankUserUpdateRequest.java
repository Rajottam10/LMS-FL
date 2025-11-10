package com.ebooks.bankservice.dtos;

import lombok.Generated;

public class BankUserUpdateRequest {
    private String fullName;
    private String mobileNumber;
    private String address;
    private String password;
    private Long accessGroupId;

    @Generated
    public String getFullName() {
        return this.fullName;
    }

    @Generated
    public String getMobileNumber() {
        return this.mobileNumber;
    }

    @Generated
    public String getAddress() {
        return this.address;
    }

    @Generated
    public String getPassword() {
        return this.password;
    }

    @Generated
    public Long getAccessGroupId() {
        return this.accessGroupId;
    }

    @Generated
    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    @Generated
    public void setMobileNumber(final String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Generated
    public void setAddress(final String address) {
        this.address = address;
    }

    @Generated
    public void setPassword(final String password) {
        this.password = password;
    }

    @Generated
    public void setAccessGroupId(final Long accessGroupId) {
        this.accessGroupId = accessGroupId;
    }
}
