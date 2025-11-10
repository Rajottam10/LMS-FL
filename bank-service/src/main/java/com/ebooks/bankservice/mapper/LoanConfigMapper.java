package com.ebooks.bankservice.mapper;

import com.ebooks.bankservice.dtos.LoanConfigRequest;
import com.ebooks.bankservice.entities.LoanConfig;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring"
)
public interface LoanConfigMapper {
    LoanConfig toEntity(LoanConfigRequest request);
}
