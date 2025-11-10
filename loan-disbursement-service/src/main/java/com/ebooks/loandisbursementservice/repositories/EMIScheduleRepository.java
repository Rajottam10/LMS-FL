package com.ebooks.loandisbursementservice.repositories;

import com.ebooks.commonmoduleloan.entities.EMISchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EMIScheduleRepository extends JpaRepository<EMISchedule, Long> {

    List<EMISchedule> findByStatusIn(List<String> statuses);

    List<EMISchedule> findByStatus(String status);

    @Query("SELECT MAX(e.installmentNumber) FROM EMISchedule e WHERE e.loanNumber = :loanNumber")
    Integer findMaxInstallmentNumberByLoanNumber(@Param("loanNumber") String loanNumber);

    // Optional: find by business key
    Optional<EMISchedule> findByLoanNumberAndInstallmentNumber(
            String loanNumber, Integer installmentNumber);
}
