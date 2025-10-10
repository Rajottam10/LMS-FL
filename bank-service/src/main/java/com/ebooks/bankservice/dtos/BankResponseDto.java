package com.ebooks.bankservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class BankResponseDto {
    private Long id;
    private String name;
    private String bankCode;
    private String address;
    private LocalDateTime createdAt;
    private BankAdminDto bankAdmin;

    public BankResponseDto(Long id, String name, String bankCode, String address,
                           LocalDate establishedDate, String status, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.bankCode = bankCode;
        this.address = address;
        this.createdAt = createdAt;
    }
}
