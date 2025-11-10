package com.ebooks.commonmoduleloan.services;


import com.ebooks.commonmoduleloan.entities.EMISchedule;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EMIScheduleService {
    List<EMISchedule> overdueEMISchedules();
    List<EMISchedule> allEMISchedules();
    void saveEmiSchedule(EMISchedule schedule);
    EMISchedule findById(Long id);
    boolean isLastInstallment(EMISchedule schedule);
}
