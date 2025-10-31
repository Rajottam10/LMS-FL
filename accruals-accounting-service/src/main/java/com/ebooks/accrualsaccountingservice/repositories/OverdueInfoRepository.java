package com.ebooks.accrualsaccountingservice.repositories;

import com.ebooks.accrualsaccountingservice.entities.OverdueInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OverdueInfoRepository extends JpaRepository<OverdueInfo, Long> {
}
