package com.ebooks.commonservice.repositories;

import com.ebooks.commonservice.entities.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
    Bank findByBankCode(String bankCode);

    boolean existsByBankCode(String bankCode);
}
