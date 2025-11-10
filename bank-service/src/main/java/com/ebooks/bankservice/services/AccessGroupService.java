package com.ebooks.bankservice.services;

import com.ebooks.bankservice.entities.BankUser;
import com.ebooks.bankservice.repositories.BankUserRepository;
import com.ebooks.commonservice.dtos.AccessGroupRequest;
import com.ebooks.commonservice.dtos.AccessGroupResponse;
import com.ebooks.commonservice.entities.AccessGroup;
import com.ebooks.commonservice.entities.AccessGroupRoleMap;
import com.ebooks.commonservice.entities.Bank;
import com.ebooks.commonservice.entities.BankAdmin;
import com.ebooks.commonservice.entities.Role;
import com.ebooks.commonservice.entities.Status;
import com.ebooks.commonservice.exceptions.StatusDoesNotExist;
import com.ebooks.commonservice.mapper.AccessGroupMapperTemporary;
import com.ebooks.commonservice.repositories.AccessGroupRepository;
import com.ebooks.commonservice.repositories.AccessGroupRoleMapRepository;
import com.ebooks.commonservice.repositories.BankAdminRepository;
import com.ebooks.commonservice.repositories.RoleRepository;
import com.ebooks.commonservice.repositories.StatusRepository;
import com.ebooks.commonservice.services.AccessGroupRoleMapService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.Generated;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccessGroupService {
    private final AccessGroupRepository accessGroupRepository;
    private final AccessGroupRoleMapService accessGroupRoleMapService;
    private final RoleRepository roleRepository;
    private final AccessGroupRoleMapRepository accessGroupRoleMapRepository;
    private final StatusRepository statusRepository;
    private final AccessGroupMapperTemporary accessGroupMapperTemp;
    private final BankAdminRepository bankAdminRepository;
    private final BankUserRepository bankUserRepository;

    @Transactional
    public Object createAccessGroup(@Valid AccessGroupRequest request, Object principal) {
        if (!"BANK".equalsIgnoreCase(request.getType())) {
            throw new RuntimeException("Bank admin can only create BANK type access groups");
        } else {
            AccessGroup accessGroup = new AccessGroup();
            accessGroup.setName(request.getName());
            accessGroup.setDescription(request.getDescription());
            accessGroup.setRecordedAt(LocalDateTime.now());
            accessGroup.setCreatedAt(LocalDateTime.now());
            if (principal instanceof BankAdmin) {
                BankAdmin admin = (BankAdmin)principal;
                accessGroup.setBank(admin.getBank());
            } else {
                if (!(principal instanceof BankUser)) {
                    throw new RuntimeException("Invalid principal type");
                }

                BankUser user = (BankUser)principal;
                accessGroup.setBank(user.getBank());
            }

            Status activeStatus = this.statusRepository.findByName("ACTIVE");
            if (activeStatus == null) {
                throw new StatusDoesNotExist("Status ACTIVE does not exist.");
            } else {
                accessGroup.setStatus(activeStatus);
                accessGroup.setType("BANK");
                this.accessGroupRepository.save(accessGroup);
                List<Role> adminRoles;
                if (principal instanceof BankAdmin) {
                    BankAdmin admin = (BankAdmin)principal;
                    adminRoles = this.accessGroupRoleMapService.getActiveRolesForAccessGroup(admin.getAccessGroup());
                } else {
                    if (!(principal instanceof BankUser)) {
                        throw new RuntimeException("Invalid principal type");
                    }

                    BankUser user = (BankUser)principal;
                    adminRoles = this.accessGroupRoleMapService.getActiveRolesForAccessGroup(user.getAccessGroup());
                }

                for(AccessGroupRequest.RoleMappingRequest roleMapping : request.getRoleGroupMaps()) {
                    Role role = (Role)this.roleRepository.findById(roleMapping.getRoleId()).orElseThrow(() -> new RuntimeException("Role not found: " + roleMapping.getRoleId()));
                    boolean allowed = adminRoles.stream().anyMatch((r) -> r.getId().equals(role.getId()));
                    if (!allowed) {
                        throw new RuntimeException("Cannot assign role: " + role.getName());
                    }

                    AccessGroupRoleMap accessGroupRoleMap = new AccessGroupRoleMap();
                    accessGroupRoleMap.setAccessGroup(accessGroup);
                    accessGroupRoleMap.setRole(role);
                    accessGroupRoleMap.setIsActive(roleMapping.getIsActive());
                    this.accessGroupRoleMapRepository.save(accessGroupRoleMap);
                }

                AccessGroupResponse response = this.accessGroupMapperTemp.toAccessGroupResponse(accessGroup);
                List<Role> roles = this.roleRepository.findAllById(request.getRoleGroupMaps().stream().map(AccessGroupRequest.RoleMappingRequest::getRoleId).toList());
                List<AccessGroupResponse.RoleResponse> roleResponses = this.accessGroupMapperTemp.toRoleResponseList(roles);
                response.setRoles(roleResponses);
                return response;
            }
        }
    }

    @Transactional(
            readOnly = true
    )
    public List<AccessGroupResponse> getAllAccessGroups(Bank bank) {
        List<AccessGroup> accessGroups = this.accessGroupRepository.findByBank(bank);
        return accessGroups.stream().map((accessGroup) -> {
            List<Role> roles = this.accessGroupRoleMapService.getActiveRolesForAccessGroup(accessGroup);
            AccessGroupResponse response = this.accessGroupMapperTemp.toAccessGroupResponse(accessGroup);
            List<AccessGroupResponse.RoleResponse> roleResponses = this.accessGroupMapperTemp.toRoleResponseList(roles);
            response.setRoles(roleResponses);
            return response;
        }).toList();
    }

    @Transactional(
            readOnly = true
    )
    public AccessGroupResponse getAccessGroupById(Long accessGroupId, Bank bank) {
        Optional<AccessGroup> accessGroupOptional = this.accessGroupRepository.findById(accessGroupId);
        if (accessGroupOptional.isEmpty()) {
            throw new RuntimeException("No access group found with id : " + accessGroupId);
        } else {
            AccessGroup accessGroup = (AccessGroup)accessGroupOptional.get();
            if (!accessGroup.getBank().getId().equals(bank.getId())) {
                throw new RuntimeException("You are not allowed to access this resource.");
            } else {
                List<Role> roles = this.accessGroupRoleMapService.getActiveRolesForAccessGroup(accessGroup);
                AccessGroupResponse response = this.accessGroupMapperTemp.toAccessGroupResponse(accessGroup);
                List<AccessGroupResponse.RoleResponse> roleResponses = this.accessGroupMapperTemp.toRoleResponseList(roles);
                response.setRoles(roleResponses);
                return response;
            }
        }
    }

    @Transactional
    public void deleteAccessGroup(Long accessGroupId, Bank bank) {
        AccessGroup accessGroup = (AccessGroup)this.accessGroupRepository.findById(accessGroupId).orElseThrow(() -> new RuntimeException("No access group found with id: " + accessGroupId));
        if (!accessGroup.getBank().getId().equals(bank.getId())) {
            throw new RuntimeException("You are not allowed to access this resource.");
        } else {
            boolean isUsedByAdmin = this.bankAdminRepository.existsByAccessGroup(accessGroup);
            if (isUsedByAdmin) {
                throw new RuntimeException("Cannot delete this access group. Delete or reassign the Bank Admins using it first.");
            } else {
                boolean isUsedByUser = this.bankUserRepository.existsByAccessGroup(accessGroup);
                if (isUsedByUser) {
                    throw new RuntimeException("Cannot delete this access group. Delete or reassign the Bank Users using it first.");
                } else {
                    List<AccessGroupRoleMap> roleMaps = this.accessGroupRoleMapRepository.findByAccessGroup(accessGroup);
                    this.accessGroupRoleMapRepository.deleteAll(roleMaps);
                    this.accessGroupRepository.delete(accessGroup);
                }
            }
        }
    }

    @Transactional
    public AccessGroupResponse updateAccessGroup(AccessGroupRequest request, Long accessGroupId, Bank bank) {
        Optional<AccessGroup> accessGroupOptional = this.accessGroupRepository.findById(accessGroupId);
        if (accessGroupOptional.isEmpty()) {
            throw new RuntimeException("No access group found with id- " + accessGroupId);
        } else {
            AccessGroup accessGroup = (AccessGroup)accessGroupOptional.get();
            if (!accessGroup.getBank().getId().equals(bank.getId())) {
                throw new RuntimeException("You are not allowed to access this resource.");
            } else {
                accessGroup.setName(request.getName());
                accessGroup.setDescription(request.getDescription());
                accessGroup.setType(request.getType());
                accessGroup.setUpdatedAt(LocalDateTime.now());
                this.accessGroupRoleMapRepository.deleteByAccessGroupId(accessGroupId);
                accessGroup = (AccessGroup)this.accessGroupRepository.save(accessGroup);

                for(AccessGroupRequest.RoleMappingRequest roleMapping : request.getRoleGroupMaps()) {
                    Role role = (Role)this.roleRepository.findById(roleMapping.getRoleId()).orElseThrow(() -> new RuntimeException("Role not found with id: " + roleMapping.getRoleId()));
                    if (!accessGroup.getType().equals(role.getType())) {
                        String var10002 = role.getType();
                        throw new RuntimeException("You cannot assign a " + var10002 + " type of role to an access group of type " + accessGroup.getType());
                    }

                    AccessGroupRoleMap accessGroupRoleMap = new AccessGroupRoleMap();
                    accessGroupRoleMap.setAccessGroup(accessGroup);
                    accessGroupRoleMap.setRole(role);
                    accessGroupRoleMap.setIsActive(roleMapping.getIsActive());
                    this.accessGroupRoleMapRepository.save(accessGroupRoleMap);
                }

                AccessGroupResponse updatedResponse = this.accessGroupMapperTemp.toAccessGroupResponse(accessGroup);
                List<Role> roles = this.roleRepository.findAllById(request.getRoleGroupMaps().stream().map(AccessGroupRequest.RoleMappingRequest::getRoleId).toList());
                List<AccessGroupResponse.RoleResponse> roleResponses = this.accessGroupMapperTemp.toRoleResponseList(roles);
                updatedResponse.setRoles(roleResponses);
                return updatedResponse;
            }
        }
    }

    @Generated
    public AccessGroupService(final AccessGroupRepository accessGroupRepository, final AccessGroupRoleMapService accessGroupRoleMapService, final RoleRepository roleRepository, final AccessGroupRoleMapRepository accessGroupRoleMapRepository, final StatusRepository statusRepository, final AccessGroupMapperTemporary accessGroupMapperTemp, final BankAdminRepository bankAdminRepository, final BankUserRepository bankUserRepository) {
        this.accessGroupRepository = accessGroupRepository;
        this.accessGroupRoleMapService = accessGroupRoleMapService;
        this.roleRepository = roleRepository;
        this.accessGroupRoleMapRepository = accessGroupRoleMapRepository;
        this.statusRepository = statusRepository;
        this.accessGroupMapperTemp = accessGroupMapperTemp;
        this.bankAdminRepository = bankAdminRepository;
        this.bankUserRepository = bankUserRepository;
    }
}
