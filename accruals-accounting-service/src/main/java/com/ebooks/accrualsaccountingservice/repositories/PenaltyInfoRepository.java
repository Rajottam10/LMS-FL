package com.ebooks.accrualsaccountingservice.repositories;

import com.ebooks.accrualsaccountingservice.entities.PenaltyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PenaltyInfoRepository extends JpaRepository<PenaltyInfo, Long> {
}
