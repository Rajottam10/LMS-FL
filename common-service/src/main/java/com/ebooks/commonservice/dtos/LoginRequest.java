package com.ebooks.commonservice.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Generated;

public class LoginRequest {
    private @NotBlank String username;
    private @NotBlank String password;

    @Generated
    public String getUsername() {
        return this.username;
    }

    @Generated
    public String getPassword() {
        return this.password;
    }

    @Generated
    public void setUsername(final String username) {
        this.username = username;
    }

    @Generated
    public void setPassword(final String password) {
        this.password = password;
    }
}
