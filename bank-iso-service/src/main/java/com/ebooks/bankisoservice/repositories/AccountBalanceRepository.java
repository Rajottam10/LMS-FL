package com.ebooks.bankisoservice.repositories;

import com.ebooks.bankisoservice.entities.AccountBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface AccountBalanceRepository extends JpaRepository<AccountBalance, Long> {

    Optional<AccountBalance> findByAccountNumber(String accountNumber);

    Optional<AccountBalance> findByAccountNumberAndBankCode(String accountNumber, String bankCode);

    boolean existsByAccountNumber(String accountNumber);

    @Modifying
    @Query("UPDATE AccountBalance ab SET ab.currentBalance = ab.currentBalance + :amount WHERE ab.accountNumber = :accountNumber")
    int updateBalance(@Param("accountNumber") String accountNumber, @Param("amount") BigDecimal amount);

    @Query("SELECT ab.currentBalance FROM AccountBalance ab WHERE ab.accountNumber = :accountNumber")
    Optional<BigDecimal> findBalanceByAccountNumber(@Param("accountNumber") String accountNumber);
}