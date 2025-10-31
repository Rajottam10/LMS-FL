package com.ebooks.commonservice.repositories;

import com.ebooks.commonservice.entities.AccessGroup;
import com.ebooks.commonservice.entities.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessGroupRepository extends JpaRepository<AccessGroup, Long> {
    Optional<AccessGroup> findByName(String name);

    List<AccessGroup> findAllByType(String type);

    List<AccessGroup> findByBank(Bank bank);
}
