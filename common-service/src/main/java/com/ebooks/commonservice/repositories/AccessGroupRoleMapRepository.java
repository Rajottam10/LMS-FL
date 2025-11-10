package com.ebooks.commonservice.repositories;

import com.ebooks.commonservice.entities.AccessGroup;
import com.ebooks.commonservice.entities.AccessGroupRoleMap;
import com.ebooks.commonservice.entities.Role;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessGroupRoleMapRepository extends JpaRepository<AccessGroupRoleMap, Long> {
    AccessGroupRoleMap findByAccessGroupAndRole(AccessGroup accessGroup, Role role);

    List<AccessGroupRoleMap> findByAccessGroup(AccessGroup accessGroup);

    void deleteByAccessGroupId(Long accessGroupId);

    @Query(
            value = "SELECT role_id FROM access_group_role_map WHERE access_group_id IN (:agIds) AND is_active = true",
            nativeQuery = true
    )
    List<Long> findActiveRoleIdsByAccessGroupIds(@Param("agIds") List<Long> accessGroupIds);

    boolean existsByAccessGroupAndRoleAndIsActiveTrue(AccessGroup accessGroup, Role role);
}
