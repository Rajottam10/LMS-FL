package com.ebooks.bankservice.dtos;

import com.ebooks.bankservice.entities.Bank;
import com.ebooks.commonservice.entities.AccessGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankUserRequestDto {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String password;

    private Bank bank;

    private Boolean isAdmin = false;

    private AccessGroup accessGroup;
}