package com.ebooks.prepaymentservice.repositories;

import com.ebooks.prepaymentservice.entities.PrepaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository for PrepaymentLog
 */
@Repository
public interface PrepaymentLogRepository extends JpaRepository<PrepaymentLog, Long> {

    List<PrepaymentLog> findByLoanNumber(String loanNumber);

    List<PrepaymentLog> findByPrepayDateBetween(LocalDate start, LocalDate end);

    List<PrepaymentLog> findByStatus(String status);
}