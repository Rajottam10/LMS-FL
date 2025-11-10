package com.ebooks.accrualsaccountingservice.services.impl;

import com.ebooks.accrualsaccountingservice.dtos.AccrualReportResponse;
import com.ebooks.commonmoduleloan.repositories.InterestInfoRepository;
import com.ebooks.commonmoduleloan.repositories.LateFeeInfoRepository;
import com.ebooks.commonmoduleloan.repositories.OverdueInfoRepository;
import com.ebooks.commonmoduleloan.repositories.PenaltyInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccrualReportService {

    private final InterestInfoRepository interestRepo;
    private final PenaltyInfoRepository penaltyRepo;
    private final OverdueInfoRepository overdueRepo;
    private final LateFeeInfoRepository lateFeeRepo;

    public List<AccrualReportResponse> getAccrualReport() {
        Map<String, AccrualReportResponse> reportMap = new HashMap<>();

        // 1. ACCUMULATE INTEREST
        interestRepo.findAll().forEach(entry -> {
            String loan = entry.getLoanNumber();
            BigDecimal amount = entry.getDailyInterestAmount();
            reportMap.computeIfAbsent(loan, AccrualReportResponse::new)
                    .addTotalInterest(amount);
        });

        // 2. ACCUMULATE PENALTY
        penaltyRepo.findAll().forEach(entry -> {
            String loan = entry.getLoanNumber();
            BigDecimal amount = entry.getPenaltyAmount();
            reportMap.computeIfAbsent(loan, AccrualReportResponse::new)
                    .addTotalPenaltyInterest(amount);  // ← ADD
        });

        // 3. OVERDUE (usually 1 per loan)
        overdueRepo.findAll().forEach(entry -> {
            String loan = entry.getLoanNumber();
            BigDecimal amount = entry.getOverdueAmount();
            reportMap.computeIfAbsent(loan, AccrualReportResponse::new)
                    .setTotalOverdueInterest(amount);  // ← OK to set
        });

        // 4. LATE FEE (usually 1 per loan)
        lateFeeRepo.findAll().forEach(entry -> {
            String loan = entry.getLoanNumber();
            BigDecimal amount = entry.getLateFeeAmount();
            reportMap.computeIfAbsent(loan, AccrualReportResponse::new)
                    .setTotalLateFee(amount);  // ← OK to set
        });

        return new ArrayList<>(reportMap.values());
    }
}