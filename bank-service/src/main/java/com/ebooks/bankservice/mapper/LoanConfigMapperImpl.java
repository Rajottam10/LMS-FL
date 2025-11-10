package com.ebooks.bankservice.mapper;

import com.ebooks.bankservice.dtos.LoanConfigRequest;
import com.ebooks.bankservice.entities.LoanConfig;
import org.springframework.stereotype.Component;

@Component
public class LoanConfigMapperImpl implements LoanConfigMapper {
    public LoanConfig toEntity(LoanConfigRequest request) {
        if (request == null) {
            return null;
        } else {
            LoanConfig loanConfig = new LoanConfig();
            loanConfig.setMinAmount(request.getMinAmount());
            loanConfig.setMaxAmount(request.getMaxAmount());
            loanConfig.setAdminFeeType(request.getAdminFeeType());
            return loanConfig;
        }
    }
}
