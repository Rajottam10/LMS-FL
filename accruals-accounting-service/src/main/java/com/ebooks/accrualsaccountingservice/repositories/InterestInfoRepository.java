package com.ebooks.accrualsaccountingservice.repositories;

import com.ebooks.accrualsaccountingservice.entities.InterestInfo;
import com.ebooks.accrualsaccountingservice.entities.LateFeeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface InterestInfoRepository extends JpaRepository<InterestInfo, Long> {
    boolean existsByLoanNumberAndInstallmentNumberAndAccrualDate(
            String loanNumber,
            Long installmentNumber,
            LocalDate accrualDate
    );
}
