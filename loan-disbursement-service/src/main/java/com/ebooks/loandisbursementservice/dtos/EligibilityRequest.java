package com.ebooks.loandisbursementservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EligibilityRequest {
    private String customerNumber;
    private String bankCode;
}
