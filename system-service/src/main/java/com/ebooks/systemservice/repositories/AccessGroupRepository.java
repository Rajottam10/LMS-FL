package com.ebooks.systemservice.repositories;

import com.ebooks.systemservice.entities.AccessGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessGroupRepository extends JpaRepository<AccessGroup, Long> {
    Optional<AccessGroup> findByName(String name);

    @Query("SELECT ag FROM AccessGroup ag LEFT JOIN FETCH ag.accessGroupRoleMaps agrm LEFT JOIN FETCH agrm.role WHERE ag.id = :id")
    Optional<AccessGroup> findByIdWithRoles(@Param("id") Long id);

    @Query("SELECT ag FROM AccessGroup ag LEFT JOIN FETCH ag.accessGroupRoleMaps agrm LEFT JOIN FETCH agrm.role")
    List<AccessGroup> findAllWithRoles();

    boolean existsByNameAndIdNot(String name, Long id);
}
