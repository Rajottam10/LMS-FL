package com.ebooks.commonmoduleloan.repositories;

import com.ebooks.commonmoduleloan.entities.PenaltyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PenaltyInfoRepository extends JpaRepository<PenaltyInfo, Long> {

    boolean existsByLoanNumberAndInstallmentNumberAndAccrualDate(String loanNumber, Integer installmentNumber, LocalDate accrualDate);
    @Query("SELECT COALESCE(SUM(e.penaltyAmount), 0) FROM PenaltyInfo e WHERE e.loanNumber = :loanNumber")
    BigDecimal getTotalPenaltyInterestForLoan(@Param("loanNumber") String loanNumber);

    @Query("SELECT COALESCE(SUM(p.penaltyAmount), 0) " +
            "FROM PenaltyInfo p " +
            "WHERE p.loanNumber = :loanNumber AND p.installmentNumber = :emiNo")
    BigDecimal getTotalPenaltyInterestForEmi(
            @Param("loanNumber") String loanNumber,
            @Param("emiNo") Integer emiNo
    );
}
