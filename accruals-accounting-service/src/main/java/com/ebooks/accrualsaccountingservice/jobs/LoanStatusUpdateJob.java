package com.ebooks.accrualsaccountingservice.jobs;

import com.ebooks.accrualsaccountingservice.services.impl.LoanDetailService;
import com.ebooks.commonmoduleloan.entities.EMISchedule;
import com.ebooks.commonmoduleloan.entities.LoanDetail;
import com.ebooks.commonmoduleloan.repositories.EMIScheduleRepository;
import com.ebooks.commonmoduleloan.services.EMIScheduleServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanStatusUpdateJob {
    private final LoanDetailService loanDetailService;
    private final EMIScheduleServiceImpl emiScheduleService;
    private final EMIScheduleRepository emiScheduleRepository;

    private static final int DEFAULT_THRESHOLD_DAYS = 90;

//    @Scheduled(cron = "0 */5 * * * *")
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


    @Scheduled(cron = "0/30 * * * * *")
    @Transactional
    public void updateEmiStatuses() {
        log.info("EMI status update job running at {}", LocalDateTime.now());

        List<EMISchedule> emiSchedules = emiScheduleService.allEMISchedules();
        LocalDate today = LocalDate.now();

        for (EMISchedule emi : emiSchedules) {

            // Skip if already PAID
            if ("PAID".equals(emi.getStatus())) {
                continue;
            }

            LocalDate demandDate = emi.getDemandDate();
            LocalDate startDate = emi.getInstallmentStartDate();

            // 1. DEFAULT: > 90 days past demand date
            long daysOverdue = ChronoUnit.DAYS.between(demandDate, today);
            if (daysOverdue > DEFAULT_THRESHOLD_DAYS && !"DEFAULT".equals(emi.getStatus())) {
                updateStatus(emi, "DEFAULT");
                continue;
            }

            // 2. OVERDUE: demandDate < today, but not yet DEFAULT
            if (demandDate.isBefore(today) && !"OVERDUE".equals(emi.getStatus()) && !"DEFAULT".equals(emi.getStatus())) {
                updateStatus(emi, "OVERDUE");
                continue;
            }

            // 3. UPCOMING: installment not started yet
            if (!"UPCOMING".equals(emi.getStatus()) && startDate.isAfter(today)) {
                updateStatus(emi, "UPCOMING");
                continue;
            }

            // 4. ACTIVE: between installmentStartDate and demandDate
            if (!"ACTIVE".equals(emi.getStatus())
                    && !today.isBefore(startDate)
                    && !today.isAfter(demandDate)) {
                updateStatus(emi, "ACTIVE");
                continue;
            }
        }
    }

    // HELPER METHOD — REUSABLE & CLEAN
    private void updateStatus(EMISchedule emi, String newStatus) {
        EMISchedule updated = emiScheduleService.findById(emi.getId());
        updated.setStatus(newStatus);
        emiScheduleService.saveEmiSchedule(updated);
        log.info("EMI {} | Loan: {} | Installment: {} → Status: {}",
                emi.getId(), emi.getLoanNumber(), emi.getInstallmentNumber(), newStatus);
    }
}
