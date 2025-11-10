package com.ebooks.systemservice.utils;

import com.ebooks.commonservice.entities.AccessGroup;
import com.ebooks.commonservice.entities.AccessGroupRoleMap;
import com.ebooks.commonservice.entities.Role;
import com.ebooks.commonservice.entities.Status;
import com.ebooks.commonservice.repositories.AccessGroupRepository;
import com.ebooks.commonservice.repositories.AccessGroupRoleMapRepository;
import com.ebooks.commonservice.repositories.RoleRepository;
import com.ebooks.commonservice.repositories.StatusRepository;
import com.ebooks.systemservice.entities.SystemUser;
import com.ebooks.systemservice.repositories.SystemUserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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

    @EventListener({ApplicationReadyEvent.class})
    @Transactional
    public void initializeSuperAdmin() {
        try {
            Status activeStatus = this.createOrGetStatus("ACTIVE", "ACTIVE");
            Status blockStatus = this.createOrGetStatus("BLOCK", "BLOCK");
            Status deleteStatus = this.createOrGetStatus("DELETE", "DELETE");
            this.initializeAllRoles();
            Role godRole = (Role)this.roleRepository.findByPermission("GOD").orElseThrow(() -> new RuntimeException("GOD role not found after initialization"));
            AccessGroup superAdminGroup = this.createOrGetAccessGroup("superadmin", "superadmin", "SYSTEM", activeStatus);
            this.assignAllRolesToSuperAdmin(superAdminGroup);
            this.createSuperAdminUser(activeStatus, superAdminGroup);
            logger.info("Super Admin initialization completed successfully");
        } catch (Exception e) {
            logger.error("Error during Super Admin initialization: {}", e.getMessage(), e);
        }

    }

    private Status createOrGetStatus(String name, String description) {
        Status existingStatus = this.statusRepository.findByName(name);
        if (existingStatus == null) {
            Status status = new Status();
            status.setName(name);
            status.setDescription(description);
            return (Status)this.statusRepository.save(status);
        } else {
            return existingStatus;
        }
    }

    private void initializeAllRoles() {
        this.createOrGetRole("GOD", "GOD", "GOD", "SYSTEM");
        this.createOrGetRole("ACCESS_GROUP", "ACCESS_GROUP", "ACCESS_GROUP", "BANK");
        this.createOrGetRole("CREATE_USER", "CREATE_USER", "CREATE_USER", "BANK");
        this.createOrGetRole("READ_USER", "READ_USER", "READ_USER", "BANK");
        this.createOrGetRole("UPDATE_USER", "UPDATE_USER", "UPDATE_USER", "BANK");
        this.createOrGetRole("DELETE_USER", "DELETE_USER", "DELETE_USER", "BANK");
        this.createOrGetRole("CREATE_BANK", "CREATE_BANK", "CREATE_BANK", "BANK");
        this.createOrGetRole("READ_BANK", "READ_BANK", "READ_BANK", "BANK");
        this.createOrGetRole("UPDATE_BANK", "UPDATE_BANK", "UPDATE_BANK", "BANK");
        this.createOrGetRole("DELETE_BANK", "DELETE_BANK", "DELETE_BANK", "BANK");
        this.createOrGetRole("LOAN_CONFIGURATION", "LOAN_CONFIGURATION", "LOAN_CONFIGURATION", "BANK");
        this.createOrGetRole("BLOCK_USER", "BLOCK_USER", "BLOCK_USER", "BANK");
        this.createOrGetRole("UNBLOCK_USER", "UNBLOCK_USER", "UNBLOCK_USER", "BANK");
        logger.info("All required roles initialized successfully");
    }

    private Role createOrGetRole(String name, String description, String permission, String type) {
        return (Role)this.roleRepository.findByPermission(permission).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            role.setDescription(description);
            role.setPermission(permission);
            role.setType(type);
            return (Role)this.roleRepository.save(role);
        });
    }

    private AccessGroup createOrGetAccessGroup(String name, String description, String type, Status status) {
        return (AccessGroup)this.accessGroupRepository.findByName(name).orElseGet(() -> {
            AccessGroup group = new AccessGroup();
            group.setName(name);
            group.setDescription(description);
            group.setType(type);
            group.setStatus(status);
            return (AccessGroup)this.accessGroupRepository.save(group);
        });
    }

    private void assignAllRolesToSuperAdmin(AccessGroup superAdminGroup) {
        List<Role> allRoles = this.roleRepository.findAll();

        for(Role role : allRoles) {
            this.createAccessGroupRoleMap(superAdminGroup, role);
        }

        logger.info("Assigned {} roles to super admin group", allRoles.size());
    }

    private void createAccessGroupRoleMap(AccessGroup accessGroup, Role role) {
        if (!this.accessGroupRoleMapRepository.existsByAccessGroupAndRoleAndIsActiveTrue(accessGroup, role)) {
            AccessGroupRoleMap map = new AccessGroupRoleMap();
            map.setAccessGroup(accessGroup);
            map.setRole(role);
            map.setIsActive(true);
            this.accessGroupRoleMapRepository.save(map);
            logger.debug("Assigned role '{}' to access group '{}'", role.getName(), accessGroup.getName());
        }

    }

    private void createSuperAdminUser(Status status, AccessGroup accessGroup) {
        SystemUser existingUser = this.systemUserRepository.findByUsername("superadmin");
        if (existingUser == null) {
            SystemUser superAdmin = new SystemUser();
            superAdmin.setFullName("superadmin");
            superAdmin.setEmail("rajottam.pradhan@foneloan.com");
            superAdmin.setUsername("superadmin");
            superAdmin.setPassword(this.passwordEncoder.encode("superadmin"));
            superAdmin.setStatus(status);
            superAdmin.setAccessGroup(accessGroup);
            this.systemUserRepository.save(superAdmin);
            logger.info("Super Admin user created successfully with email: rajottam.pradhan@foneloan.com");
        } else {
            existingUser.setFullName("superadmin");
            existingUser.setEmail("rajottam.pradhan@foneloan.com");
            existingUser.setPassword(this.passwordEncoder.encode("superadmin"));
            this.systemUserRepository.save(existingUser);
        }

    }
}
