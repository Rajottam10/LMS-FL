package com.ebooks.bankservice.mapper;

import com.ebooks.bankservice.dtos.BankUserRequest;
import com.ebooks.bankservice.dtos.BankUserResponse;
import com.ebooks.bankservice.entities.BankUser;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring"
)
public interface BankUserMapper {
    BankUser toEntity(BankUserRequest bankUserRequest);

    BankUserResponse toResponse(BankUser bankUser);
}
