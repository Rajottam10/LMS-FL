package com.ebooks.prepaymentservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrepaymentInquiry {
    private PayableDetails payableDetails;
    private LoanDetails loanDetails;
}
