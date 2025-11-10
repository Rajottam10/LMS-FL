package com.ebooks.commonmoduleloan.repositories;

import com.ebooks.commonmoduleloan.entities.LateFeeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface LateFeeInfoRepository extends JpaRepository<LateFeeInfo, Long> {
    boolean existsByLoanNumberAndInstallmentNumber(String loanNumber, Integer installmentNumber);
    @Query("SELECT COALESCE(SUM(e.lateFeeAmount), 0) FROM LateFeeInfo e WHERE e.loanNumber = :loanNumber")
    BigDecimal getTotalLateFeeForLoan(@Param("loanNumber") String loanNumber);

    @Query("SELECT COALESCE(SUM(l.lateFeeAmount), 0) " +
            "FROM LateFeeInfo l " +
            "WHERE l.loanNumber = :loanNumber AND l.installmentNumber = :emiNo")
    BigDecimal getTotalLateFeeForEmi(
            @Param("loanNumber") String loanNumber,
            @Param("emiNo") Integer emiNo
    );
}
