package com.ebooks.systemservice.utils;

import com.ebooks.systemservice.entities.AccessGroup;
import com.ebooks.systemservice.entities.AccessGroupRoleMap;
import com.ebooks.systemservice.entities.Status;
import com.ebooks.systemservice.entities.SystemUser;
import com.ebooks.systemservice.repositories.AccessGroupRepository;
import com.ebooks.systemservice.repositories.AccessGroupRoleMapRepository;
import com.ebooks.systemservice.repositories.RoleRepository;
import com.ebooks.systemservice.repositories.StatusRepository;
import com.ebooks.systemservice.repositories.SystemUserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ebooks.systemservice.entities.Role;

@Component
public class SuperAdminInitializer {

    private static final Logger logger = LoggerFactory.getLogger(SuperAdminInitializer.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private AccessGroupRepository accessGroupRepository;

    @Autowired
    private AccessGroupRoleMapRepository accessGroupRoleMapRepository;

    @Autowired
    private SystemUserRepository systemUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeSuperAdmin() {
        try {
            Status activeStatus = createOrGetStatus("ACTIVE", "ACTIVE");
            Status inactiveStatus = createOrGetStatus("DELETE", "DELETE");

            Role godRole = createOrGetRole("GOD", "GOD Role", "GOD");

            AccessGroup superAdminGroup = createOrGetAccessGroup("superadmin", activeStatus);

            createAccessGroupRoleMap(superAdminGroup, godRole);

            createSuperAdminUser(activeStatus, superAdminGroup);

            logger.info("Super Admin initialization completed successfully");

        } catch (Exception e) {
            logger.error("Error during Super Admin initialization: {}", e.getMessage(), e);
        }
    }

    private Status createOrGetStatus(String name, String description) {
        return statusRepository.findByName(name)
                .orElseGet(() -> {
                    Status status = new Status();
                    status.setName(name);
                    status.setDescription(description);
                    return statusRepository.save(status);
                });
    }

    private Role createOrGetRole(String name, String description, String permission) {
        return roleRepository.findByPermission(permission)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(name);
                    role.setDescription(description);
                    role.setPermission(permission);
                    return roleRepository.save(role);
                });
    }

    private AccessGroup createOrGetAccessGroup(String name, Status status) {
        return accessGroupRepository.findByName(name)
                .orElseGet(() -> {
                    AccessGroup group = new AccessGroup();
                    group.setName(name);
                    group.setStatus(status);
                    return accessGroupRepository.save(group);
                });
    }

    private void createAccessGroupRoleMap(AccessGroup accessGroup, Role role) {
        if (!accessGroupRoleMapRepository.existsByAccessGroupAndRoleAndIsActiveTrue(accessGroup, role)) {
            AccessGroupRoleMap map = new AccessGroupRoleMap();
            map.setAccessGroup(accessGroup);
            map.setRole(role);
            map.setIsActive(true);
            accessGroupRoleMapRepository.save(map);
        }
    }

    private void createSuperAdminUser(Status status, AccessGroup accessGroup) {
        if (systemUserRepository.findByUsername("superadmin").isEmpty()) {
            SystemUser superAdmin = new SystemUser();
            superAdmin.setName("Super Administrator");
            superAdmin.setEmail("superadmin@system.com");
            superAdmin.setUsername("superadmin");
            superAdmin.setPassword(passwordEncoder.encode("superadmin")); // Change in production
            superAdmin.setStatus(status);
            superAdmin.setAccessGroup(accessGroup);

            systemUserRepository.save(superAdmin);
            logger.info("Super Admin user created successfully");
        } else {
            logger.info("Super Admin user already exists");
        }
    }
}