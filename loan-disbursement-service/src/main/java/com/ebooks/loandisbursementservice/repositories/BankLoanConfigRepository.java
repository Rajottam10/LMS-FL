package com.ebooks.loandisbursementservice.repositories;

import com.ebooks.loandisbursementservice.entities.BankLoanConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankLoanConfigRepository extends JpaRepository<BankLoanConfig, Long> {
    Optional<BankLoanConfig> findBankLoanConfigByBankCode(String bankCode);
}
