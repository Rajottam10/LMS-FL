package com.ebooks.commonservice.repositories;

import com.ebooks.commonservice.entities.AccessGroup;
import com.ebooks.commonservice.entities.AccessGroupRoleMap;
import com.ebooks.commonservice.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessGroupRoleMapRepository extends JpaRepository<AccessGroupRoleMap, Long> {
    List<AccessGroupRoleMap> findByAccessGroupAndIsActiveTrue(AccessGroup accessGroup);
    boolean existsByAccessGroupAndRoleAndIsActiveTrue(AccessGroup accessGroup, Role role);
    @Modifying
    @Query("UPDATE AccessGroupRoleMap agrm SET agrm.isActive = false WHERE agrm.accessGroup = :accessGroup")
    void deactivateAllMappingsForAccessGroup(@Param("accessGroup") AccessGroup accessGroup);
}
