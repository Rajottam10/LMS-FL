package com.ebooks.bankservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankAdminLoginResponseDto {
    private String token;
    private String message;
    private String username;
    private Long bankId;
    private String bankName;
}
