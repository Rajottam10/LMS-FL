//package com.ebooks.accrualsaccountingservice.services.impl;
//
//import com.ebooks.commonmoduleloan.entities.EMISchedule;
//import com.ebooks.commonmoduleloan.repositories.EMIScheduleRepository;
//import com.ebooks.commonmoduleloan.services.EMIScheduleService;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class EMIScheduleServiceImpl implements EMIScheduleService {
//
//    private final EMIScheduleRepository emiScheduleRepository;
//
//    public EMIScheduleServiceImpl(EMIScheduleRepository emiScheduleRepository) {
//        this.emiScheduleRepository = emiScheduleRepository;
//    }
//
//    @Override
//    public List<EMISchedule> overdueEMISchedules() {
//        return emiScheduleRepository.findByStatus("OVERDUE");
//    }
//
//    @Override
//    public List<EMISchedule> allEMISchedules() {
//        return emiScheduleRepository.findAll();
//    }
//
//    public EMISchedule findById(Long id) {
//        return emiScheduleRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("EMI schedule not found: " + id));
//    }
//
//    public void saveEmiSchedule(EMISchedule updatedEmiSchedule) {
//        emiScheduleRepository.save(updatedEmiSchedule);
//    }
//
//    public boolean isLastInstallment(EMISchedule schedule) {
//        Integer max = emiScheduleRepository.findMaxInstallmentNumberByLoanNumber(schedule.getLoanNumber());
//        return max != null && schedule.getInstallmentNumber().equals(max);
//    }
//}