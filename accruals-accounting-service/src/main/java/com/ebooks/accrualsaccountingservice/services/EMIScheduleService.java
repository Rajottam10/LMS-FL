package com.ebooks.accrualsaccountingservice.services;

import com.ebooks.accrualsaccountingservice.entities.EMISchedule;

import java.util.List;

public interface EMIScheduleService {

    List<EMISchedule> activeEMISchedules();
    List<EMISchedule> overdueEMISchedules();
    List<EMISchedule> allEMISchedules();
    void saveEmiSchedule(EMISchedule schedule);
    EMISchedule findById(Long id);
    boolean isLastInstallment(EMISchedule schedule);
}
