package com.ebooks.bankservice.repositories;


import com.ebooks.bankservice.entities.BankUser;
import com.ebooks.commonservice.entities.AccessGroup;
import com.ebooks.commonservice.entities.Bank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankUserRepository extends JpaRepository<BankUser, Long> {
    BankUser findBankUserByEmail(String email);

    Page<BankUser> findBankUserByBank(Bank bank, Pageable pageable);

    boolean existsByAccessGroup(AccessGroup accessGroup);

//    Optional<BankUser> findByEmail(String email);
}
