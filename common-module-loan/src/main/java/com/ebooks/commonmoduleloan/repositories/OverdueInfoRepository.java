package com.ebooks.commonmoduleloan.repositories;

import com.ebooks.commonmoduleloan.entities.OverdueInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface OverdueInfoRepository extends JpaRepository<OverdueInfo, Long> {
    boolean existsByLoanNumberAndInstallmentNumberAndAccrualDate(String loanNumber, Integer installmentNumber, LocalDate d);
    @Query("SELECT COALESCE(SUM(e.overdueAmount), 0) FROM OverdueInfo e WHERE e.loanNumber = :loanNumber")
    BigDecimal getTotalOverdueInterestForLoan(@Param("loanNumber") String loanNumber);

    @Query("SELECT COALESCE(SUM(o.overdueAmount), 0) FROM OverdueInfo o " +
            "WHERE o.loanNumber = :loanNumber AND o.installmentNumber = :emiNo")
    BigDecimal getTotalOverdueInterestForEmi(
            @Param("loanNumber") String loanNumber,
            @Param("emiNo") Integer emiNo
    );
}
