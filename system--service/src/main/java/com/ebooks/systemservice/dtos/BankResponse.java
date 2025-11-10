package com.ebooks.systemservice.dtos;

import lombok.Generated;

public class BankResponse {
    private String name;
    private String bankCode;
    private Boolean isActive;
    private String address;
    private AdminResponse admin;

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public String getBankCode() {
        return this.bankCode;
    }

    @Generated
    public Boolean getIsActive() {
        return this.isActive;
    }

    @Generated
    public String getAddress() {
        return this.address;
    }

    @Generated
    public AdminResponse getAdmin() {
        return this.admin;
    }

    @Generated
    public void setName(final String name) {
        this.name = name;
    }

    @Generated
    public void setBankCode(final String bankCode) {
        this.bankCode = bankCode;
    }

    @Generated
    public void setIsActive(final Boolean isActive) {
        this.isActive = isActive;
    }

    @Generated
    public void setAddress(final String address) {
        this.address = address;
    }

    @Generated
    public void setAdmin(final AdminResponse admin) {
        this.admin = admin;
    }

    @Generated
    public BankResponse(final String name, final String bankCode, final Boolean isActive, final String address, final AdminResponse admin) {
        this.name = name;
        this.bankCode = bankCode;
        this.isActive = isActive;
        this.address = address;
        this.admin = admin;
    }

    public static class AdminResponse {
        private String fullName;
        private String email;
        private String username;
        private Long accessGroupId;

        @Generated
        public String getFullName() {
            return this.fullName;
        }

        @Generated
        public String getEmail() {
            return this.email;
        }

        @Generated
        public String getUsername() {
            return this.username;
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
        public void setEmail(final String email) {
            this.email = email;
        }

        @Generated
        public void setUsername(final String username) {
            this.username = username;
        }

        @Generated
        public void setAccessGroupId(final Long accessGroupId) {
            this.accessGroupId = accessGroupId;
        }

        @Generated
        public AdminResponse(final String fullName, final String email, final String username, final Long accessGroupId) {
            this.fullName = fullName;
            this.email = email;
            this.username = username;
            this.accessGroupId = accessGroupId;
        }
    }
}
