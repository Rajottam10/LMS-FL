package com.ebooks.accrualsaccountingservice.jobs;

import com.ebooks.accrualsaccountingservice.entities.EMISchedule;
import com.ebooks.accrualsaccountingservice.entities.LoanDetail;
import com.ebooks.accrualsaccountingservice.services.EMIScheduleService;
import com.ebooks.accrualsaccountingservice.services.impl.LoanDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanStatusUpdateJob {
    private final LoanDetailService loanDetailService;
    private final EMIScheduleService emiScheduleService;

    private static final int DEFAULT_THRESHOLD_DAYS = 90;

    @Scheduled(cron = "0 */5 * * * *")
    public void updateLoanStatuses() {
        log.info("Loan update job running at {}", LocalDateTime.now());
        List<EMISchedule> emiSchedules = emiScheduleService.allEMISchedules();

        for (EMISchedule emiSchedule : emiSchedules) {
            LoanDetail loanDetail = loanDetailService.getLoanByLoanNumber(emiSchedule.getLoanNumber());
            String currentLoanStatus = loanDetail.getStatus();
            String emiStatus = emiSchedule.getStatus();

            if ("DEFAULT".equals(emiStatus) && !"DEFAULT".equals(currentLoanStatus)) {
                loanDetail.setStatus("DEFAULT");
                loanDetailService.saveLoanDetail(loanDetail);
            } else if ("OVERDUE".equals(emiStatus) && !"DEFAULT".equals(currentLoanStatus)
                    && !"OVERDUE".equals(currentLoanStatus)) {
                loanDetail.setStatus("OVERDUE");
                loanDetailService.saveLoanDetail(loanDetail);
            }
        }
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void updateEmiStatuses() {
        log.info("EMI status update job running at {}", LocalDateTime.now());
        List<EMISchedule> emiSchedules = emiScheduleService.allEMISchedules();
        LocalDate today = LocalDate.now();

        for (EMISchedule emiSchedule : emiSchedules) {
            if (emiSchedule.getStatus().equals("PAID")) {
                continue;
            }
            long days = ChronoUnit.DAYS.between(emiSchedule.getDemandDate(), today);
            if (days > DEFAULT_THRESHOLD_DAYS && !emiSchedule.getStatus().equals("DEFAULT")) {
                EMISchedule updatedEmiSchedule = emiScheduleService.findById(emiSchedule.getId());
                updatedEmiSchedule.setStatus("DEFAULT");
                emiScheduleService.saveEmiSchedule(updatedEmiSchedule);
            } else if (emiSchedule.getDemandDate().isBefore(today) && !emiSchedule.getStatus().equals("OVERDUE")
                    && !emiSchedule.getStatus().equals("DEFAULT")) {
                EMISchedule updatedEmiSchedule = emiScheduleService.findById(emiSchedule.getId());
                updatedEmiSchedule.setStatus("OVERDUE");
                emiScheduleService.saveEmiSchedule(updatedEmiSchedule);
            }
            if(!emiSchedule.getStatus().equals("UPCOMING") && emiSchedule.getInstallmentStartDate().isAfter(today)){
                EMISchedule updatedEmiSchedule = emiScheduleService.findById(emiSchedule.getId());
                updatedEmiSchedule.setStatus("UPCOMING");
                emiScheduleService.saveEmiSchedule(updatedEmiSchedule);
            }
            if(!emiSchedule.getStatus().equals("ACTIVE") && !today.isBefore(emiSchedule.getInstallmentStartDate())
            && !today.isAfter(emiSchedule.getDemandDate())){
                EMISchedule updatedEmiSchedule = emiScheduleService.findById(emiSchedule.getId());
                updatedEmiSchedule.setStatus("ACTIVE");
                emiScheduleService.saveEmiSchedule(updatedEmiSchedule);
            }
        }
    }
}
