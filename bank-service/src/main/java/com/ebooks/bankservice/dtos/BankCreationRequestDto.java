package com.ebooks.bankservice.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BankCreationRequestDto {
    @NotBlank(message = "Bank name is required")
    private String name;

    @NotBlank(message = "Bank code is required")
    private String bankCode;

    private String address;

    private LocalDate establishedDate;

    @NotBlank(message = "Admin username is required")
    private String adminUsername;

    @NotBlank(message = "Admin password is required")
    private String adminPassword;

    @Email(message = "Valid admin email is required")
    @NotBlank(message = "Admin email is required")
    private String adminEmail;

    @NotBlank(message = "Admin full name is required")
    private String adminFullName;
}

