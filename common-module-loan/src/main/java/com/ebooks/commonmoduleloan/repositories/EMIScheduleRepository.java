package com.ebooks.commonmoduleloan.repositories;

import com.ebooks.commonmoduleloan.entities.EMISchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EMIScheduleRepository extends JpaRepository<EMISchedule, Long> {

    List<EMISchedule> findByStatus(String status);

    @Query("SELECT MAX(e.installmentNumber) FROM EMISchedule e WHERE e.loanNumber = :loanNumber")
    Integer findMaxInstallmentNumberByLoanNumber(@Param("loanNumber") String loanNumber);

//    Optional<EMISchedule> findByLoanNumberAndInstallmentNumber(
//            String loanNumber, Integer installmentNumber);
//
//    List<EMISchedule> findByStatusAndDemandDateBefore(String status, LocalDate date);

    // Optional: Other useful queries
    List<EMISchedule> findByLoanNumber(String loanNumber);

    List<EMISchedule> findByLoanNumberAndStatus(String loanNumber, String pending);

    @Query("SELECT COALESCE(SUM(e.paidPrincipal), 0) FROM EMISchedule e WHERE e.loanNumber = :loanNumber")
    BigDecimal totalPaidPrincipalByLoanNumber(@Param("loanNumber") String loanNumber);

    @Query("SELECT COALESCE(SUM(e.paidInterest), 0) FROM EMISchedule e WHERE e.loanNumber = :loanNumber")
    BigDecimal totalPaidInterestByLoanNumber(@Param("loanNumber") String loanNumber);

    @Query("SELECT COALESCE(SUM(e.paidOverdueInterest), 0) FROM EMISchedule e WHERE e.loanNumber = :loanNumber")
    BigDecimal totalPaidOverdueInterestByLoanNumber(@Param("loanNumber") String loanNumber);

    @Query("SELECT COALESCE(SUM(e.paidPenaltyInterest), 0) FROM EMISchedule e WHERE e.loanNumber = :loanNumber")
    BigDecimal totalPaidPenaltyInterestByLoanNumber(@Param("loanNumber") String loanNumber);

    @Query("SELECT COALESCE(SUM(e.paidLateFee), 0) FROM EMISchedule e WHERE e.loanNumber = :loanNumber")
    BigDecimal totalPaidLateFeeByLoanNumber(@Param("loanNumber") String loanNumber);

    @Query("SELECT e FROM EMISchedule e WHERE e.demandDate <= :today AND e.status <> :paid")
    List<EMISchedule> findAllByEmiDateLessThanEqualAndStatusNot(@Param("today") LocalDate today,
                                                                @Param("paid") String paid);}
