package com.ebooks.commonservice.repositories;

import com.ebooks.commonservice.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByPermission(String permission);
    Optional<Role> findByName(String name);
}

