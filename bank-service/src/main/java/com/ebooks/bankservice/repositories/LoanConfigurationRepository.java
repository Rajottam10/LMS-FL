package com.ebooks.bankservice.repositories;

import com.ebooks.bankservice.entities.LoanConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanConfigurationRepository extends JpaRepository<LoanConfiguration, Long> {
    List<LoanConfiguration> findByBankId(Long bankId);
    Optional<LoanConfiguration> findByBankIdAndIsActiveTrue(Long bankId);
    boolean existsByBankIdAndIsActiveTrue(Long bankId);
}