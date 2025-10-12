package com.ebooks.bankservice.repositories;

import com.ebooks.bankservice.entities.Bank;
import com.ebooks.bankservice.entities.BankUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankUserRepository extends JpaRepository<BankUser, Long> {
    Optional<BankUser> findByUsername(String username);
    Optional<BankUser> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<BankUser> findByBank(Bank bank);
    Page<BankUser> findByBank(Bank bank, Pageable pageable);
}