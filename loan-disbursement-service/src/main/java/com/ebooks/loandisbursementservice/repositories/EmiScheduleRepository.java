package com.ebooks.loandisbursementservice.repositories;

import com.ebooks.loandisbursementservice.entities.EmiSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmiScheduleRepository extends JpaRepository<EmiSchedule, Long> {
}
