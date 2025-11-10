package com.ebooks.commonservice.repositories;

import com.ebooks.commonservice.entities.AccessGroup;
import com.ebooks.commonservice.entities.BankAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BankAdminRepository extends JpaRepository<BankAdmin, Long> {
    BankAdmin findByUsername(String username);

    boolean existsByUsername(String username);

    @Modifying
    @Transactional
    @Query("update BankAdmin b set b.accessGroup = null where b.accessGroup.id = :accessGroupId")
    void setAccessGroupNull(Long accessGroupId);

    boolean existsByAccessGroup(AccessGroup accessGroup);
}
