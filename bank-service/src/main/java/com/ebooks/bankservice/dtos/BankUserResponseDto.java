package com.ebooks.bankservice.dtos;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class BankUserResponseDto {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String status;
    private Boolean isAdmin;
    private Long bankId;
    private String bankName;
    private Long accessGroupId;
    private String accessGroupName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}