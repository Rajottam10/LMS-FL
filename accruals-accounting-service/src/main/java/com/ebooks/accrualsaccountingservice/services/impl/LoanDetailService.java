package com.ebooks.accrualsaccountingservice.services.impl;


import com.ebooks.accrualsaccountingservice.entities.LoanDetail;
import com.ebooks.accrualsaccountingservice.repositories.LoanDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanDetailService {
    private final LoanDetailRepository loanDetailRepository;

    public List<LoanDetail> getAllLoans(){
        List<LoanDetail> loanDetails = loanDetailRepository.findAll();
        if (loanDetails.isEmpty()){
            throw new RuntimeException("No loans found.");
        }
        return loanDetails;
    }
    public LoanDetail getLoanByLoanNumber(String loanNumber){
        LoanDetail loanDetail = loanDetailRepository.findLoanDetailByLoanNumber(loanNumber);
        if (loanDetail == null){
            throw new RuntimeException("No loan found with loan number " + loanNumber);
        }
        return loanDetail;
    }

    public void saveLoanDetail(LoanDetail loanDetail) {
        loanDetailRepository.save(loanDetail);
    }
}
