package com.ebooks.bankservice.repositories;

import com.ebooks.bankservice.entities.BankUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankAdminRepository extends JpaRepository<BankUser, Long> {
    Optional<String> findByFullName(String fullName);
    boolean existsByUsername(String username);
    List<BankUser> findByBankIdAndIsAdminFalse(Long bankId);
    boolean existsByEmail(@Email(message = "Valid admin email is required") @NotBlank(message = "Admin email is required") String adminEmail);
    Optional<BankUser> findByUsername(String username);

    Optional<BankUser> findFirstByIsAdminTrue();

    Page<BankUser> findByBankIdAndIsAdminFalse(Long currentBankId, Pageable pageable);
}