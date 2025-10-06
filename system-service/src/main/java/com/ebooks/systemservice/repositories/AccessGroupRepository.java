package com.ebooks.systemservice.repositories;

import com.ebooks.systemservice.entities.AccessGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccessGroupRepository extends JpaRepository<AccessGroup, Long> {
    Optional<AccessGroup> findByName(String name);
}
