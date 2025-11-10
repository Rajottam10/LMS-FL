package com.ebooks.loandisbursementservice.repositories;

import com.ebooks.commonmoduleloan.entities.LoanDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanDetailRepository extends JpaRepository<LoanDetail, Long> {
}