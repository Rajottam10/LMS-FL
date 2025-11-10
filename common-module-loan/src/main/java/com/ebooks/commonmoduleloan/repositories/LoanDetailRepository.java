package com.ebooks.commonmoduleloan.repositories;

import com.ebooks.commonmoduleloan.entities.LoanDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanDetailRepository extends JpaRepository<LoanDetail, Long> {
    Optional<LoanDetail> findLoanDetailByLoanNumber(String loanNumber);
}
