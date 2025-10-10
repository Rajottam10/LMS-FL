package com.ebooks.bankservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BankUserResponseDto {
    private Long id;
    private String email;
    private String username;
    private String fullName;
    private String status;
    private Long bankId;
    private String bankName;
    private Long accessGroupId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
