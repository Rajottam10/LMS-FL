package com.ebooks.systemservice.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Generated;

public class BankUpdateRequest {
    private @NotBlank String name;
    private @NotBlank String address;
    private @NotNull Boolean isActive;
    @NotNull
    private @NotNull AdminUpdateRequest admin;

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
    public AdminUpdateRequest getAdmin() {
        return this.admin;
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
    public void setAdmin(final AdminUpdateRequest admin) {
        this.admin = admin;
    }

    public static class AdminUpdateRequest {
        private @NotBlank String fullName;
        private @NotBlank @Email String email;
        private @NotBlank String password;
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
        public void setAccessGroupId(final Long accessGroupId) {
            this.accessGroupId = accessGroupId;
        }
    }
}
