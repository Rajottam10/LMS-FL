package com.ebooks.bankservice.mapper;

import com.ebooks.bankservice.dtos.BankUserRequest;
import com.ebooks.bankservice.dtos.BankUserResponse;
import com.ebooks.bankservice.entities.BankUser;
import org.springframework.stereotype.Component;

@Component
public class BankUserMapperImpl implements BankUserMapper {
    public BankUser toEntity(BankUserRequest bankUserRequest) {
        if (bankUserRequest == null) {
            return null;
        } else {
            BankUser bankUser = new BankUser();
            bankUser.setFullName(bankUserRequest.getFullName());
            bankUser.setEmail(bankUserRequest.getEmail());
            bankUser.setMobileNumber(bankUserRequest.getMobileNumber());
            bankUser.setAddress(bankUserRequest.getAddress());
            bankUser.setPassword(bankUserRequest.getPassword());
            return bankUser;
        }
    }

    public BankUserResponse toResponse(BankUser bankUser) {
        if (bankUser == null) {
            return null;
        } else {
            BankUserResponse bankUserResponse = new BankUserResponse();
            bankUserResponse.setId(bankUser.getId());
            bankUserResponse.setFullName(bankUser.getFullName());
            bankUserResponse.setEmail(bankUser.getEmail());
            bankUserResponse.setMobileNumber(bankUser.getMobileNumber());
            bankUserResponse.setAddress(bankUser.getAddress());
            bankUserResponse.setCreatedAt(bankUser.getCreatedAt());
            bankUserResponse.setUpdatedAt(bankUser.getUpdatedAt());
            return bankUserResponse;
        }
    }
}
