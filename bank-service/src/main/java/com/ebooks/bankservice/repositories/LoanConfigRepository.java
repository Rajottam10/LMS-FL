package com.ebooks.bankservice.repositories;

import com.ebooks.bankservice.entities.LoanConfig;
import com.ebooks.commonservice.entities.Bank;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanConfigRepository extends JpaRepository<LoanConfig, Long> {
    List<LoanConfig> findLoanConfigByBank(Bank bank);
}
