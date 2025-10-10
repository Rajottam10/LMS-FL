package com.ebooks.bankservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankAdminDto {
    private Long id;
    private String username;
    private String email;
    private String fullName;

    public BankAdminDto(Long id, String username, String email, String fullName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
    }

    public BankAdminDto() {
    }
}
