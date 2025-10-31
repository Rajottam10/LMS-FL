package com.ebooks.accrualsaccountingservice.repositories;

import com.ebooks.accrualsaccountingservice.entities.EMISchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EMIScheduleRepository extends JpaRepository<EMISchedule, Long> {
    Optional<List<EMISchedule>> findAllByStatus(String string);
    @Query("SELECT MAX(e.installmentNumber) FROM EMISchedule e WHERE e.loanNumber = :loanNumber")
    Integer findMaxInstallmentNumberByLoanNumber(String loanNumber);
}
