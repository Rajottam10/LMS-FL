package com.ebooks.accrualsaccountingservice.services.impl;

import com.ebooks.accrualsaccountingservice.services.InterestInfoService;
import com.ebooks.accrualsaccountingservice.services.LateFeeInfoService;
import com.ebooks.accrualsaccountingservice.services.OverdueInfoService;
import com.ebooks.accrualsaccountingservice.services.PenaltyInfoService;
import com.ebooks.commonmoduleloan.services.AccrualService;
import com.ebooks.commonmoduleloan.entities.EMISchedule;
import com.ebooks.commonmoduleloan.entities.InterestInfo;
import com.ebooks.commonmoduleloan.entities.LateFeeInfo;
import com.ebooks.commonmoduleloan.entities.LoanDetail;
import com.ebooks.commonmoduleloan.entities.OverdueInfo;
import com.ebooks.commonmoduleloan.entities.PenaltyInfo;
import com.ebooks.commonmoduleloan.services.EMIScheduleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class AccrualServiceImpl implements AccrualService {

    private final InterestInfoService interestInfoService;
    private final LateFeeInfoService lateFeeInfoService;
    private final OverdueInfoService overdueInfoService;
    private final PenaltyInfoService penaltyInfoService;
    private final EMIScheduleServiceImpl emiScheduleService;
    private final LoanDetailService loanDetailService;

    public AccrualServiceImpl(
            InterestInfoService interestInfoService,
            LateFeeInfoService lateFeeInfoService,
            OverdueInfoService overdueInfoService,
            PenaltyInfoService penaltyInfoService,
            EMIScheduleServiceImpl emiScheduleService,
            LoanDetailService loanDetailService) {
        this.interestInfoService = interestInfoService;
        this.lateFeeInfoService = lateFeeInfoService;
        this.overdueInfoService = overdueInfoService;
        this.penaltyInfoService = penaltyInfoService;
        this.emiScheduleService = emiScheduleService;
        this.loanDetailService = loanDetailService;
    }

    @Override
    @Transactional
    public void processDailyAccruals() {
        LocalDate today = LocalDate.now();
        log.info("=== DAILY ACCRUAL JOB STARTED @ {} ===", today);

        List<EMISchedule> schedules = emiScheduleService.allEMISchedules();

        for (EMISchedule emi : schedules) {
            if (isPaidOrDefault(emi) || isBeforeStart(emi, today)) {
                continue;
            }

            LoanDetail loan = loanDetailService.getLoanByLoanNumber(emi.getLoanNumber());
            if (loan == null) {
                log.warn("Loan not found for EMI {} (Loan: {})", emi.getId(), emi.getLoanNumber());
                continue;
            }

            // 1. REGULAR INTEREST: From installmentStartDate → today

            LocalDate interestStart = emi.getInstallmentStartDate();
            LocalDate interestEnd = emiScheduleService.isLastInstallment(emi) ? today : emi.getDemandDate().minusDays(1);

            if (interestEnd.isAfter(today)) interestEnd = today;

            for (LocalDate date = interestStart; !date.isAfter(interestEnd); date = date.plusDays(1)) {
                if (interestInfoService.existsByLoanAndInstallmentAndDate(
                        emi.getLoanNumber(), emi.getInstallmentNumber(), date)) {
                    continue;
                }

                BigDecimal dailyInterest = calculateDailyInterest(emi, loan);
                InterestInfo info = InterestInfo.builder()
                        .loanNumber(emi.getLoanNumber())
                        .installmentNumber(emi.getInstallmentNumber())
                        .accrualDate(date)
                        .dailyInterestAmount(dailyInterest)
                        .build();
                interestInfoService.createAccrual(info);
                log.debug("Interest {} accrued on {} for EMI {}", dailyInterest, date, emi.getId());
            }

            // 2. OVERDUE ACCRUALS: Only AFTER demandDate
            if (today.isAfter(emi.getDemandDate())) {

                // ——— PENALTY INTEREST: Daily from demandDate + 1 → today ———
                LocalDate penaltyStart = emi.getDemandDate().plusDays(1);
                LocalDate penaltyEnd = today;

                for (LocalDate d = penaltyStart; !d.isAfter(penaltyEnd); d = d.plusDays(1)) {
                    if (penaltyInfoService.existsByLoanAndInstallmentAndDate(
                            emi.getLoanNumber(), emi.getInstallmentNumber(), d)) {
                        continue;
                    }

                    BigDecimal dailyPenalty = calculateDailyPenalty(emi, loan);
                    PenaltyInfo pInfo = PenaltyInfo.builder()
                            .loanNumber(emi.getLoanNumber())
                            .installmentNumber(emi.getInstallmentNumber())
                            .accrualDate(d)
                            .penaltyAmount(dailyPenalty)
                            .build();
                    penaltyInfoService.createPenalty(pInfo);
                    log.debug("Penalty ₹{} accrued on {} for EMI {}", dailyPenalty, d, emi.getId());
                }

                // ——— OVERDUE INTEREST: Daily from demandDate + 1 → today ———
                for (LocalDate d = penaltyStart; !d.isAfter(penaltyEnd); d = d.plusDays(1)) {
                    if (overdueInfoService.existsByLoanAndInstallmentAndDate(
                            emi.getLoanNumber(), emi.getInstallmentNumber(), d)) {
                        continue;
                    }

                    BigDecimal dailyOverdue = calculateDailyOverdue(emi, loan);
                    OverdueInfo oInfo = OverdueInfo.builder()
                            .loanNumber(emi.getLoanNumber())
                            .installmentNumber(emi.getInstallmentNumber())
                            .accrualDate(d)
                            .overdueAmount(dailyOverdue)
                            .build();
                    overdueInfoService.createOverdue(oInfo);
                    log.debug("Overdue interest ₹{} accrued on {} for EMI {}", dailyOverdue, d, emi.getId());
                }

                // ——— LATE FEE: ONE TIME ONLY ———
                if (!lateFeeInfoService.existsByLoanAndInstallment(
                        emi.getLoanNumber(), emi.getInstallmentNumber())) {

                    BigDecimal lateFeeAmount = loan.getLateFee() != null
                            ? loan.getLateFee()
                            : BigDecimal.valueOf(100.00);

                    LateFeeInfo lateFee = LateFeeInfo.builder()
                            .loanNumber(emi.getLoanNumber())
                            .installmentNumber(emi.getInstallmentNumber())
                            .accrualDate(today)
                            .lateFeeAmount(lateFeeAmount)
                            .build();

                    lateFeeInfoService.createLateFee(lateFee);
                    log.info("Late fee ₹{} applied on EMI {} (Loan: {})",
                            lateFeeAmount, emi.getId(), emi.getLoanNumber());
                }
            }
        }

        log.info("=== DAILY ACCRUAL JOB COMPLETED ===");
    }

    // ———————————————————— HELPER METHODS ————————————————————

    private boolean isPaidOrDefault(EMISchedule emi) {
        return "PAID".equals(emi.getStatus()) || "DEFAULT".equals(emi.getStatus());
    }

    private boolean isBeforeStart(EMISchedule emi, LocalDate today) {
        return today.isBefore(emi.getInstallmentStartDate());
    }

    private BigDecimal calculateDailyInterest(EMISchedule emi, LoanDetail loan) {
        BigDecimal rate = loan.getAnnualInterestRate() != null
                ? loan.getAnnualInterestRate()
                : BigDecimal.valueOf(12.00);
        return emi.getBeginningBalance().multiply(rate).divide(BigDecimal.valueOf(100*365),4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDailyPenalty(EMISchedule emi, LoanDetail loan) {
        BigDecimal rate = loan.getPenaltyRate() != null ? loan.getPenaltyRate() : BigDecimal.valueOf(2.00);
        return emi.getPrincipal().multiply(rate).divide(BigDecimal.valueOf(100*365),4,RoundingMode.HALF_UP);

    }

    private BigDecimal calculateDailyOverdue(EMISchedule emi, LoanDetail loan) {
        BigDecimal rate = loan.getOverdueRate() != null ? loan.getOverdueRate() : BigDecimal.valueOf(16.00);
        return emi.getPrincipal().multiply(rate).divide(BigDecimal.valueOf(100*365),4, RoundingMode.HALF_UP);
    }
}