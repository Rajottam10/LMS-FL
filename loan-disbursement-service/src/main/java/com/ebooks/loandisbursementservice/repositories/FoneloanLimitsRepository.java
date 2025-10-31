package com.ebooks.loandisbursementservice.repositories;

import com.ebooks.loandisbursementservice.entities.FoneloanLimits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoneloanLimitsRepository extends JpaRepository<FoneloanLimits, Long> {
    List<FoneloanLimits> findByCustomerNumberAndBankCode(String customerNumber, String bankCode);
}
