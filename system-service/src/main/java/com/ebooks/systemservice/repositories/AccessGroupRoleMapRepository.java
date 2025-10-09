package com.ebooks.systemservice.repositories;

import com.ebooks.systemservice.entities.AccessGroup;
import com.ebooks.systemservice.entities.AccessGroupRoleMap;
import com.ebooks.systemservice.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessGroupRoleMapRepository extends JpaRepository<AccessGroupRoleMap, Long> {
    List<AccessGroupRoleMap> findByAccessGroupAndIsActiveTrue(AccessGroup accessGroup);
    boolean existsByAccessGroupAndRoleAndIsActiveTrue(AccessGroup accessGroup, Role role);
    void deleteByAccessGroup(AccessGroup accessGroup);
}
