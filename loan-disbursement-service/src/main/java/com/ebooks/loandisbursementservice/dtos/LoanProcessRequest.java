package com.ebooks.loandisbursementservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanProcessRequest {
    private String customerNumber;
    private String bankCode;
}
