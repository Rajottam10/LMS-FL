package com.ebooks.accrualsaccountingservice.services.impl;

import com.ebooks.accrualsaccountingservice.entities.EMISchedule;
import com.ebooks.accrualsaccountingservice.entities.InterestInfo;
import com.ebooks.accrualsaccountingservice.entities.LateFeeInfo;
import com.ebooks.accrualsaccountingservice.entities.OverdueInfo;
import com.ebooks.accrualsaccountingservice.entities.PenaltyInfo;
import com.ebooks.accrualsaccountingservice.services.AccrualService;
import com.ebooks.accrualsaccountingservice.services.EMIScheduleService;
import com.ebooks.accrualsaccountingservice.services.InterestInfoService;
import com.ebooks.accrualsaccountingservice.services.LateFeeInfoService;
import com.ebooks.accrualsaccountingservice.services.OverdueInfoService;
import com.ebooks.accrualsaccountingservice.services.PenaltyInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private final EMIScheduleService emiScheduleService;
    public AccrualServiceImpl(
            InterestInfoService interestInfoService,
            LateFeeInfoService lateFeeInfoService,
            OverdueInfoService overdueInfoService,
            PenaltyInfoService penaltyInfoService,
            EMIScheduleService eMIScheduleService) {
        this.emiScheduleService = eMIScheduleService;
        this.interestInfoService = interestInfoService;
        this.lateFeeInfoService = lateFeeInfoService;
        this.overdueInfoService = overdueInfoService;
        this.penaltyInfoService = penaltyInfoService;
    }

    @Override
    public void processDailyAccruals() {
        LocalDate today = LocalDate.now();
//        LocalDate today = LocalDate.of(2025, 2, 1);


        List<EMISchedule> activeSchedules = emiScheduleService.activeEMISchedules();

        for (EMISchedule schedule : activeSchedules) {

            boolean isAccrued = interestInfoService.existsByLoanAndInstallmentAndDate(
                    schedule.getLoanNumber(),
                    schedule.getInstallmentNumber(),
                    today
            );
            if (isAccrued) continue;

            // Skip accruals after demand date unless it's the last installment
            if (today.isBefore(schedule.getInstallmentStartDate())) {
                continue;
            }

            //  Interest
            BigDecimal dailyInterest = accrueInterest(schedule);
            InterestInfo accrualInterest =  new InterestInfo();
            accrualInterest.setAccrualDate(today);
            accrualInterest.setDailyInterestAmount(dailyInterest);
            accrualInterest.setInstallmentNumber(schedule.getInstallmentNumber());
            accrualInterest.setLoanNumber(schedule.getLoanNumber());
            interestInfoService.createAccrual(accrualInterest);
            log.info("Interest accrued for loan {} installment {}: {}", schedule.getLoanNumber(), schedule.getInstallmentNumber(), dailyInterest);

            if (today.isAfter(schedule.getDemandDate())) {
                BigDecimal dailyPenalty = accruePenalty(schedule);
                PenaltyInfo penaltyInfo = new PenaltyInfo();
                penaltyInfo.setAccrualDate(today);
                penaltyInfo.setPenaltyAmount(dailyPenalty);
                penaltyInfo.setInstallmentNumber(schedule.getInstallmentNumber());
                penaltyInfo.setLoanNumber(schedule.getLoanNumber());
                penaltyInfoService.createPenalty(penaltyInfo);
                log.info("Penalty accrued for loan {} installment {}: {}", schedule.getLoanNumber(), schedule.getInstallmentNumber(), dailyPenalty);

                // Overdue Interest
                BigDecimal dailyOverdue = accrueOverdueInterest(schedule);
                OverdueInfo overdueInfo = new OverdueInfo();
                overdueInfo.setAccrualDate(today);
                overdueInfo.setOverdueAmount(dailyOverdue);
                overdueInfo.setInstallmentNumber(schedule.getInstallmentNumber());
                overdueInfo.setLoanNumber(schedule.getLoanNumber());
                overdueInfoService.createOverdue(overdueInfo);
                log.info("Overdue interest accrued for loan {} installment {}: {}", schedule.getLoanNumber(), schedule.getInstallmentNumber(), dailyOverdue);

                //Late Fee
                LateFeeInfo lateFeeInfo = new LateFeeInfo();
                lateFeeInfo.setAccrualDate(today);
                lateFeeInfo.setLateFeeAmount(BigDecimal.valueOf(200.00));
                lateFeeInfo.setInstallmentNumber(schedule.getInstallmentNumber());
                lateFeeInfo.setLoanNumber(schedule.getLoanNumber());
                lateFeeInfoService.createLateFee(lateFeeInfo);
                log.info("Late Fee accrued for loan {} installment {}: {}", schedule.getLoanNumber(), schedule.getInstallmentNumber(), "200.00");
            }
        }
    }

    private BigDecimal accrueInterest(EMISchedule emi) {
        //(Principal * rate) / (100 * 365)
        return emi.getBeginningBalance()
                .multiply(BigDecimal.valueOf(17))
                .divide(BigDecimal.valueOf(100 * 365),4, RoundingMode.HALF_UP);
    }

    private BigDecimal accruePenalty(EMISchedule emi) {
         //(Overdue Principal * penaltyRate) / (100 * 365)
        return emi.getPrincipal()
                .multiply(BigDecimal.valueOf(2))
                .divide(BigDecimal.valueOf(100 * 365), RoundingMode.HALF_UP);
    }

    private BigDecimal accrueOverdueInterest(EMISchedule emi) {
        //(Overdue Principal * rate) / (100 * 365)
        return emi.getPrincipal()
                .multiply(BigDecimal.valueOf(7))
                .divide(BigDecimal.valueOf(100 * 365), RoundingMode.HALF_UP);
    }
}

