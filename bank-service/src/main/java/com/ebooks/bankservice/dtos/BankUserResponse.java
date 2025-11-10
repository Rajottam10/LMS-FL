package com.ebooks.bankservice.dtos;

import java.time.LocalDateTime;
import lombok.Generated;

public class BankUserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String mobileNumber;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public String getFullName() {
        return this.fullName;
    }

    @Generated
    public String getEmail() {
        return this.email;
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
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Generated
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    @Generated
    public void setId(final Long id) {
        this.id = id;
    }

    @Generated
    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    @Generated
    public void setEmail(final String email) {
        this.email = email;
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
    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Generated
    public void setUpdatedAt(final LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
