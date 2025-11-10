
package com.ebooks.systemservice.services.implementation;

import com.ebooks.commonservice.dtos.AccessGroupRequest;
import com.ebooks.commonservice.dtos.AccessGroupResponse;
import com.ebooks.commonservice.entities.AccessGroup;
import com.ebooks.commonservice.entities.AccessGroupRoleMap;
import com.ebooks.commonservice.entities.Role;
import com.ebooks.commonservice.entities.Status;
import com.ebooks.commonservice.mapper.AccessGroupMapperTemporary;
import com.ebooks.commonservice.repositories.AccessGroupRepository;
import com.ebooks.commonservice.repositories.AccessGroupRoleMapRepository;
import com.ebooks.commonservice.repositories.BankAdminRepository;
import com.ebooks.commonservice.repositories.RoleRepository;
import com.ebooks.commonservice.repositories.StatusRepository;
import com.ebooks.commonservice.services.AccessGroupRoleMapService;
import com.ebooks.systemservice.services.AccessGroupService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.Generated;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccessGroupServiceImpl implements AccessGroupService {
    private final AccessGroupRepository accessGroupRepository;
    private final RoleRepository roleRepository;
    private final AccessGroupRoleMapRepository accessGroupRoleMapRepository;
    private final StatusRepository statusRepository;
    private final AccessGroupMapperTemporary accessGroupMapperTemp;
    private final AccessGroupRoleMapService accessGroupRoleMapService;
    private final BankAdminRepository bankAdminRepository;

    @Transactional(
            readOnly = true
    )
    public List<AccessGroupResponse> getAllAccessGroup() {
        List<AccessGroup> accessGroups = this.accessGroupRepository.findAll();
        List<AccessGroupResponse> accessGroupResponses = accessGroups.stream().filter((accessGroup) -> accessGroup.getBank() == null).map((accessGroup) -> {
            List<Role> roles = this.accessGroupRoleMapService.getActiveRolesForAccessGroup(accessGroup);
            AccessGroupResponse response = this.accessGroupMapperTemp.toAccessGroupResponse(accessGroup);
            List<AccessGroupResponse.RoleResponse> roleResponses = this.accessGroupMapperTemp.toRoleResponseList(roles);
            response.setRoles(roleResponses);
            return response;
        }).toList();
        return accessGroupResponses;
    }

    @Transactional
    public AccessGroupResponse createAccessGroup(AccessGroupRequest request) {
        AccessGroup accessGroup = this.accessGroupMapperTemp.toAccessGroup(request);
        accessGroup.setRecordedAt(LocalDateTime.now());
        accessGroup.setCreatedAt(LocalDateTime.now());
        Status activeStatus = this.statusRepository.findByName("ACTIVE");
        if (activeStatus == null) {
            throw new RuntimeException("Status ACTIVE does not exist.");
        } else {
            accessGroup.setStatus(activeStatus);
            accessGroup = (AccessGroup)this.accessGroupRepository.save(accessGroup);

            for(AccessGroupRequest.RoleMappingRequest roleMapping : request.getRoleGroupMaps()) {
                Role role = (Role)this.roleRepository.findById(roleMapping.getRoleId()).orElseThrow(() -> new RuntimeException("Role with ID " + roleMapping.getRoleId() + " not found"));
                if (!role.getType().equals(accessGroup.getType())) {
                    throw new RuntimeException("Role type mismatch: Please select role of type " + accessGroup.getType() + " only for this access group.");
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

    @Transactional(
            readOnly = true
    )
    public AccessGroupResponse findAccessGroupById(Long accessGroupId) {
        Optional<AccessGroup> accessGroupOptional = this.accessGroupRepository.findById(accessGroupId);
        if (accessGroupOptional.isEmpty()) {
            throw new RuntimeException("AccessGroup not found with id: " + accessGroupId);
        } else {
            AccessGroup accessGroup = (AccessGroup)accessGroupOptional.get();
            if (accessGroup.getBank() != null) {
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
    public void deleteAccessGroup(Long accessGroupId) {
        Optional<AccessGroup> accessGroupOptional = this.accessGroupRepository.findById(accessGroupId);
        if (accessGroupOptional.isEmpty()) {
            throw new RuntimeException("AccessGroup not found with id: " + accessGroupId);
        } else {
            AccessGroup accessGroup = (AccessGroup)accessGroupOptional.get();
            if (accessGroup.getBank() != null) {
                throw new RuntimeException("You are not allowed to access this resource.");
            } else {
                this.bankAdminRepository.setAccessGroupNull(accessGroupId);

                for(AccessGroupRoleMap roleMap : this.accessGroupRoleMapRepository.findByAccessGroup(accessGroup)) {
                    if (roleMap.getAccessGroup().getId().equals(accessGroup.getId())) {
                        this.accessGroupRoleMapRepository.delete(roleMap);
                    }
                }

                this.accessGroupRepository.delete(accessGroup);
            }
        }
    }

    @Transactional
    public AccessGroupResponse updateAccessGroup(Long accessGroupId, AccessGroupRequest request) {
        Optional<AccessGroup> existingOptionalAccessGroup = this.accessGroupRepository.findById(accessGroupId);
        if (existingOptionalAccessGroup.isEmpty()) {
            throw new RuntimeException("AccessGroup not found with id: " + accessGroupId);
        } else {
            AccessGroup accessGroup = (AccessGroup)existingOptionalAccessGroup.get();
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

    @Generated
    public AccessGroupServiceImpl(final AccessGroupRepository accessGroupRepository, final RoleRepository roleRepository, final AccessGroupRoleMapRepository accessGroupRoleMapRepository, final StatusRepository statusRepository, final AccessGroupMapperTemporary accessGroupMapperTemp, final AccessGroupRoleMapService accessGroupRoleMapService, final BankAdminRepository bankAdminRepository) {
        this.accessGroupRepository = accessGroupRepository;
        this.roleRepository = roleRepository;
        this.accessGroupRoleMapRepository = accessGroupRoleMapRepository;
        this.statusRepository = statusRepository;
        this.accessGroupMapperTemp = accessGroupMapperTemp;
        this.accessGroupRoleMapService = accessGroupRoleMapService;
        this.bankAdminRepository = bankAdminRepository;
    }
}
