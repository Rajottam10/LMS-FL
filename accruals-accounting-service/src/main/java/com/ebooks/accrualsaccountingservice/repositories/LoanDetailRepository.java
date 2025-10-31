package com.ebooks.accrualsaccountingservice.repositories;

import com.ebooks.accrualsaccountingservice.entities.LoanDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanDetailRepository extends JpaRepository<LoanDetail, Long> {
    LoanDetail findLoanDetailByLoanNumber(String loanNumber);
}
