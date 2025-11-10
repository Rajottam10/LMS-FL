package com.ebooks.commonmoduleloan.repositories;

import com.ebooks.commonmoduleloan.entities.InterestInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface InterestInfoRepository extends JpaRepository<InterestInfo, Long> {
    boolean existsByLoanNumberAndInstallmentNumberAndAccrualDate(
            String loanNumber,
            Integer installmentNumber,
            LocalDate accrualDate
    );

    @Query("SELECT COALESCE(SUM(e.dailyInterestAmount), 0) FROM InterestInfo e WHERE e.loanNumber = :loanNumber")
    BigDecimal getDailyAccrualsForLoan(@Param("loanNumber") String loanNumber);

    @Query("SELECT COALESCE(SUM(i.dailyInterestAmount), 0) " +
            "FROM InterestInfo i " +
            "WHERE i.loanNumber = :loanNumber AND i.installmentNumber = :emiNo")
    BigDecimal getTotalInterestForEmi(
            @Param("loanNumber") String loanNumber,
            @Param("emiNo") Integer emiNo  // ← MUST match :emiNo in query
    );
}
