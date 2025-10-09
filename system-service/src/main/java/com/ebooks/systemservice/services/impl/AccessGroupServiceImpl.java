package com.ebooks.systemservice.services.impl;

import com.ebooks.commonservice.dtos.AccessGroupRequestDto;
import com.ebooks.commonservice.dtos.AccessGroupResponseDto;
import com.ebooks.commonservice.dtos.RoleDto;
import com.ebooks.commonservice.dtos.RoleGroupMapDto;
import com.ebooks.systemservice.entities.AccessGroup;
import com.ebooks.systemservice.entities.AccessGroupRoleMap;
import com.ebooks.systemservice.entities.Role;
import com.ebooks.systemservice.entities.Status;
import com.ebooks.systemservice.repositories.AccessGroupRepository;
import com.ebooks.systemservice.repositories.AccessGroupRoleMapRepository;
import com.ebooks.systemservice.repositories.RoleRepository;
import com.ebooks.systemservice.repositories.StatusRepository;
import com.ebooks.systemservice.services.AccessGroupService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccessGroupServiceImpl implements AccessGroupService {

    @Autowired
    private AccessGroupRepository accessGroupRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccessGroupRoleMapRepository accessGroupRoleMapRepository;

    @Override
    @Transactional
    public AccessGroupResponseDto createAccessGroup(AccessGroupRequestDto requestDTO) {
        // Check if name already exists
        if (accessGroupRepository.findByName(requestDTO.getName()).isPresent()) {
            throw new RuntimeException("Access group with name '" + requestDTO.getName() + "' already exists");
        }

        // Get status
        Status status = statusRepository.findById(requestDTO.getStatusId())
                .orElseThrow(() -> new RuntimeException("Status not found with id: " + requestDTO.getStatusId()));

        // Create access group
        AccessGroup accessGroup = new AccessGroup();
        accessGroup.setName(requestDTO.getName());
        accessGroup.setDescription(requestDTO.getDescription());
        accessGroup.setType(requestDTO.getType());
        accessGroup.setStatus(status);

        AccessGroup savedAccessGroup = accessGroupRepository.save(accessGroup);

        // Assign roles if provided
        if (requestDTO.getRoleGroupMaps() != null && !requestDTO.getRoleGroupMaps().isEmpty()) {
            assignRolesToAccessGroup(savedAccessGroup, requestDTO.getRoleGroupMaps());
        }

        return convertToResponseDTO(savedAccessGroup);
    }

    @Override
    public List<AccessGroupResponseDto> getAllAccessGroups() {
        return accessGroupRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AccessGroupResponseDto getAccessGroupById(Long id) {
        AccessGroup accessGroup = accessGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Access group not found with id: " + id));
        return convertToResponseDTO(accessGroup);
    }

    @Override
    @Transactional
    public AccessGroupResponseDto updateAccessGroup(Long id, AccessGroupRequestDto requestDTO) {
        AccessGroup accessGroup = accessGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Access group not found with id: " + id));

        // Check if name already exists (excluding current record)
        if (accessGroupRepository.existsByNameAndIdNot(requestDTO.getName(), id)) {
            throw new RuntimeException("Access group with name '" + requestDTO.getName() + "' already exists");
        }

        // Get status
        Status status = statusRepository.findById(requestDTO.getStatusId())
                .orElseThrow(() -> new RuntimeException("Status not found with id: " + requestDTO.getStatusId()));

        // Update access group
        accessGroup.setName(requestDTO.getName());
        accessGroup.setDescription(requestDTO.getDescription());
        accessGroup.setType(requestDTO.getType());
        accessGroup.setStatus(status);

        // Update roles
        if (requestDTO.getRoleGroupMaps() != null) {
            // Remove existing role mappings
            accessGroupRoleMapRepository.deleteByAccessGroup(accessGroup);

            // Add new role mappings
            if (!requestDTO.getRoleGroupMaps().isEmpty()) {
                assignRolesToAccessGroup(accessGroup, requestDTO.getRoleGroupMaps());
            }
        }

        AccessGroup updatedAccessGroup = accessGroupRepository.save(accessGroup);
        return convertToResponseDTO(updatedAccessGroup);
    }

    @Override
    @Transactional
    public void deleteAccessGroup(Long id) {
        AccessGroup accessGroup = accessGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Access group not found with id: " + id));
        accessGroupRepository.delete(accessGroup);
    }

    @Override
    public List<AccessGroupResponseDto> getAccessGroupsByType(String type) {
        return List.of();
    }

    private void assignRolesToAccessGroup(AccessGroup accessGroup, List<RoleGroupMapDto> roleGroupMaps) {
        for (RoleGroupMapDto roleGroupMap : roleGroupMaps) {
            if (roleGroupMap.getIsActive()) {
                Role role = roleRepository.findById(roleGroupMap.getMapId())
                        .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleGroupMap.getMapId()));

                AccessGroupRoleMap roleMap = new AccessGroupRoleMap();
                roleMap.setAccessGroup(accessGroup);
                roleMap.setRole(role);
                roleMap.setIsActive(true);
                accessGroupRoleMapRepository.save(roleMap);
            }
        }
    }

    private AccessGroupResponseDto convertToResponseDTO(AccessGroup accessGroup) {
        AccessGroupResponseDto responseDTO = new AccessGroupResponseDto();
        responseDTO.setId(accessGroup.getId());
        responseDTO.setName(accessGroup.getName());
        responseDTO.setDescription(accessGroup.getDescription());
        responseDTO.setType(accessGroup.getType());
        responseDTO.setStatusId(accessGroup.getStatus().getId());
        responseDTO.setStatusName(accessGroup.getStatus().getName());
        responseDTO.setCreatedDate(accessGroup.getCreatedDate());
        responseDTO.setUpdatedDate(accessGroup.getUpdatedDate());

        // Convert roles
        List<RoleDto> roleDTOs = accessGroup.getAccessGroupRoleMaps().stream()
                .map(roleMap -> {
                    RoleDto roleDTO = new RoleDto();
                    roleDTO.setId(roleMap.getRole().getId());
                    roleDTO.setName(roleMap.getRole().getName());
                    roleDTO.setPermission(roleMap.getRole().getPermission());
                    roleDTO.setDescription(roleMap.getRole().getDescription());
                    roleDTO.setIsActive(roleMap.getIsActive());
                    return roleDTO;
                })
                .collect(Collectors.toList());

        responseDTO.setRoles(roleDTOs);
        return responseDTO;
    }
}