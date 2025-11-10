package com.ebooks.prepaymentservice.repositories;

import com.ebooks.prepaymentservice.entities.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {

    List<PaymentRecord> findByLoanNumber(String loanNumber);

    List<PaymentRecord> findByPaymentDate(LocalDate date);

    List<PaymentRecord> findByTypeAndPaymentDateBetween(String type, LocalDate start, LocalDate end);
}