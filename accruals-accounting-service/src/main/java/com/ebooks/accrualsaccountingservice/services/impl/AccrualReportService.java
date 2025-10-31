package com.ebooks.accrualsaccountingservice.services.impl;

import com.ebooks.accrualsaccountingservice.dtos.AccrualReportResponse;
import com.ebooks.accrualsaccountingservice.entities.InterestInfo;
import com.ebooks.accrualsaccountingservice.entities.LateFeeInfo;
import com.ebooks.accrualsaccountingservice.entities.OverdueInfo;
import com.ebooks.accrualsaccountingservice.entities.PenaltyInfo;
import com.ebooks.accrualsaccountingservice.repositories.InterestInfoRepository;
import com.ebooks.accrualsaccountingservice.repositories.LateFeeInfoRepository;
import com.ebooks.accrualsaccountingservice.repositories.OverdueInfoRepository;
import com.ebooks.accrualsaccountingservice.repositories.PenaltyInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccrualReportService {
    private final InterestInfoRepository emiAccountingInterestInfoRepository;
    private final PenaltyInfoRepository emiAccountingPenaltyInterestInfoRepository;
    private final OverdueInfoRepository emiAccountingOverdueInterestInfoRepository;
    private final LateFeeInfoRepository emiAccountingLateFeeInfoRepository;

    public List<AccrualReportResponse> getAccrualReport() {
        Map<String, AccrualReportResponse> reportMap = new HashMap<>();

        List<InterestInfo> interestList = emiAccountingInterestInfoRepository.findAll();
        for (InterestInfo entry : interestList) {
            reportMap
                    .computeIfAbsent(entry.getLoanNumber(), AccrualReportResponse::new)
                    .setTotalInterest(entry.getDailyInterestAmount());
        }

        List<PenaltyInfo> penaltyList = emiAccountingPenaltyInterestInfoRepository.findAll();
        for (PenaltyInfo entry : penaltyList) {
            reportMap
                    .computeIfAbsent(entry.getLoanNumber(), AccrualReportResponse::new)
                    .setTotalPenaltyInterest(entry.getPenaltyAmount());
        }

        List<OverdueInfo> overdueList = emiAccountingOverdueInterestInfoRepository.findAll();
        for (OverdueInfo entry : overdueList) {
            reportMap
                    .computeIfAbsent(entry.getLoanNumber(), AccrualReportResponse::new)
                    .setTotalOverdueInterest(entry.getOverdueAmount());
        }

        List<LateFeeInfo> lateFeeList = emiAccountingLateFeeInfoRepository.findAll();
        for (LateFeeInfo entry : lateFeeList) {
            reportMap
                    .computeIfAbsent(entry.getLoanNumber(), AccrualReportResponse::new)
                    .setTotalLateFee(entry.getLateFeeAmount());
        }

        return new ArrayList<>(reportMap.values());
    }
}
