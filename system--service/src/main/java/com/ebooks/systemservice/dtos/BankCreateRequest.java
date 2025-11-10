package com.ebooks.systemservice.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Generated;

public class BankCreateRequest {
    private @NotBlank String bankCode;
    private @NotBlank String name;
    private @NotBlank String address;
    private @NotNull Boolean isActive;
    @NotNull
    private @NotNull AdminRequest admin;

    @Generated
    public String getBankCode() {
        return this.bankCode;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public String getAddress() {
        return this.address;
    }

    @Generated
    public Boolean getIsActive() {
        return this.isActive;
    }

    @Generated
    public AdminRequest getAdmin() {
        return this.admin;
    }

    @Generated
    public void setBankCode(final String bankCode) {
        this.bankCode = bankCode;
    }

    @Generated
    public void setName(final String name) {
        this.name = name;
    }

    @Generated
    public void setAddress(final String address) {
        this.address = address;
    }

    @Generated
    public void setIsActive(final Boolean isActive) {
        this.isActive = isActive;
    }

    @Generated
    public void setAdmin(final AdminRequest admin) {
        this.admin = admin;
    }

    public static class AdminRequest {
        private @NotBlank String fullName;
        private @NotBlank @Email String email;
        private @NotBlank String password;
        private @NotBlank String username;
        private @NotBlank Long accessGroupId;

        @Generated
        public String getFullName() {
            return this.fullName;
        }

        @Generated
        public String getEmail() {
            return this.email;
        }

        @Generated
        public String getPassword() {
            return this.password;
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
        public void setPassword(final String password) {
            this.password = password;
        }

        @Generated
        public void setUsername(final String username) {
            this.username = username;
        }

        @Generated
        public void setAccessGroupId(final Long accessGroupId) {
            this.accessGroupId = accessGroupId;
        }
    }
}
