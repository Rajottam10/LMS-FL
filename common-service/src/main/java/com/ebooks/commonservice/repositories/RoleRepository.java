package com.ebooks.commonservice.repositories;

import com.ebooks.commonservice.entities.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

    List<Role> findAllByType(String type);

    Optional<Role> findByPermission(String permission);
}
