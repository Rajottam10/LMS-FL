package com.ebooks.systemservice.repositories;

import com.ebooks.systemservice.entities.BankAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.datatransfer.StringSelection;
import java.util.Optional;

public interface BankAdminRepository extends JpaRepository<BankAdmin, Long> {
    Optional<String> findByFullName(String fullName);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
