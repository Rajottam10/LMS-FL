package com.ebooks.accrualsaccountingservice.services;

import com.ebooks.accrualsaccountingservice.entities.EMISchedule;
import com.ebooks.accrualsaccountingservice.entities.InterestInfo;
import com.ebooks.accrualsaccountingservice.entities.OverdueInfo;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface OverdueInfoService {
    void createOverdue(OverdueInfo overdueInfo);
}
