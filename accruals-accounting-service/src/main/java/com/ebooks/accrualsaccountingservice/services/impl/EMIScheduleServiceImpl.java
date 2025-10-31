package com.ebooks.accrualsaccountingservice.services.impl;

import com.ebooks.accrualsaccountingservice.entities.EMISchedule;
import com.ebooks.accrualsaccountingservice.repositories.EMIScheduleRepository;
import com.ebooks.accrualsaccountingservice.services.EMIScheduleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EMIScheduleServiceImpl implements EMIScheduleService {
    private final EMIScheduleRepository emiScheduleRepository;

    public EMIScheduleServiceImpl(EMIScheduleRepository emiScheduleRepository){
        this.emiScheduleRepository = emiScheduleRepository;
    }

    @Override
    public List<EMISchedule> activeEMISchedules() {
        List<EMISchedule> activeSchedules = emiScheduleRepository.findAllByStatus("ACTIVE").orElseThrow(()-> new RuntimeException("No active emi schedules were found."));
        return activeSchedules;
    }

    @Override
    public List<EMISchedule> overdueEMISchedules() {
        List<EMISchedule> overdueSchedules = emiScheduleRepository.findAllByStatus("OVERDUE").orElseThrow(()-> new RuntimeException("No active emi schedules were found."));
        return overdueSchedules;
    }

    @Override
    public List<EMISchedule> allEMISchedules() {
        List<EMISchedule> allSchedules = emiScheduleRepository.findAll();
        return allSchedules;
    }

    public EMISchedule findById(Long id) {
        return emiScheduleRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Emi schedule not found.")
        );
    }

    public void saveEmiSchedule(EMISchedule updatedEmiSchedule) {
        emiScheduleRepository.save(updatedEmiSchedule);
    }

    public boolean isLastInstallment(EMISchedule schedule) {
        Integer maxInstallment = emiScheduleRepository.findMaxInstallmentNumberByLoanNumber(schedule.getLoanNumber());
        return schedule.getInstallmentNumber().equals(maxInstallment);
    }
}
