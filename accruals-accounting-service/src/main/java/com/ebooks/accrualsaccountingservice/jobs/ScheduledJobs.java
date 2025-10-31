package com.ebooks.accrualsaccountingservice.jobs;

import com.ebooks.accrualsaccountingservice.services.AccrualService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduledJobs {
    private final AccrualService accrualService;

    public ScheduledJobs(AccrualService accrualService){
        this.accrualService = accrualService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void dailyInterest(){
        log.info("The daily interest method is running...");
        accrualService.processDailyAccruals();
        log.info("The daily interest method is completed successfully");
    }
}
