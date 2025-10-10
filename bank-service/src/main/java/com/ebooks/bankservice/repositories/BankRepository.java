package com.ebooks.bankservice.repositories;

import com.ebooks.bankservice.entities.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
    Optional<Bank> findBankByBankCode(String bankCode);
    boolean existsByBankCode(String bankCode);

}
