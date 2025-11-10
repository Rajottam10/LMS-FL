package com.ebooks.prepaymentservice.repositories;

import com.ebooks.prepaymentservice.entities.PrepaymentConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository for PrepaymentConfig
 * Provides built-in CRUD + custom finder by bankCode
 */
@Repository
public interface PrepaymentConfigRepository extends JpaRepository<PrepaymentConfig, String> {

    Optional<PrepaymentConfig> findByBankCode(String bankCode);
}