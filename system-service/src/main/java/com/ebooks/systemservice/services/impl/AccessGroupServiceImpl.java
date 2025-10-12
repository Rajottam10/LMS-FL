package com.ebooks.systemservice.services.impl;

import com.ebooks.commonservice.dtos.AccessGroupRequestDto;
import com.ebooks.commonservice.dtos.AccessGroupResponseDto;
import com.ebooks.commonservice.dtos.RoleGroupMapDto;
import com.ebooks.commonservice.entities.AccessGroup;
import com.ebooks.commonservice.entities.AccessGroupRoleMap;
import com.ebooks.commonservice.entities.Role;
import com.ebooks.commonservice.entities.Status;
import com.ebooks.commonservice.repositories.AccessGroupRepository;
import com.ebooks.commonservice.repositories.AccessGroupRoleMapRepository;
import com.ebooks.commonservice.repositories.RoleRepository;
import com.ebooks.commonservice.repositories.StatusRepository;
import com.ebooks.systemservice.mapper.AccessGroupMapper;
import com.ebooks.systemservice.services.AccessGroupService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccessGroupServiceImpl implements AccessGroupService {
    private final AccessGroupRepository accessGroupRepository;
    private final RoleRepository roleRepository;
    private final AccessGroupRoleMapRepository accessGroupRoleMapRepository;
    private final StatusRepository statusRepository;
    private final AccessGroupMapper accessGroupMapper;

    public AccessGroupServiceImpl(
            AccessGroupRepository accessGroupRepository,
            RoleRepository roleRepository,
            AccessGroupRoleMapRepository accessGroupRoleMapRepository,
            StatusRepository statusRepository,
            AccessGroupMapper accessGroupMapper){
        this.accessGroupRepository = accessGroupRepository;
        this.roleRepository = roleRepository;
        this.accessGroupRoleMapRepository = accessGroupRoleMapRepository;
        this.statusRepository = statusRepository;
        this.accessGroupMapper = accessGroupMapper;
    }

    @Override
    public AccessGroupResponseDto createAccessGroup(AccessGroupRequestDto request) {
        if(accessGroupRepository.findByName(request.getName()).isPresent()){
            throw new RuntimeException("AccessGroup with the name "+request.getName()+", already exists.");
        }
        Status activeStatus = statusRepository.findByName("ACTIVE").orElseThrow(()-> new RuntimeException("Active status not found."));

        AccessGroup accessGroup = new AccessGroup();
        accessGroup.setName(request.getName());
        accessGroup.setDescription(request.getDescription());
        accessGroup.setType(request.getType());
        accessGroup.setStatus(activeStatus);

        AccessGroup savedAccessGroup = accessGroupRepository.save(accessGroup);
        if (request.getRoleGroupMaps() != null) {
            createRoleMappings(savedAccessGroup, request.getRoleGroupMaps());
        }

        return accessGroupMapper.mapToResponseDto(savedAccessGroup);

    }

    @Override
    public AccessGroupResponseDto getAccessGroupById(Long id) {
        AccessGroup accessGroup = accessGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Access group not found with id: " + id));
        return accessGroupMapper.mapToResponseDto(accessGroup);
    }

    @Override
    public List<AccessGroupResponseDto> getAllAccessGroup() {
        List<AccessGroup> accessGroups = accessGroupRepository.findAll();
        return accessGroups.stream()
                .map(accessGroupMapper::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public AccessGroupResponseDto updateAccessGroup(Long id, AccessGroupRequestDto request) {
        AccessGroup accessGroup = accessGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Access group not found with id: " + id));

        if (!accessGroup.getName().equals(request.getName()) &&
                accessGroupRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Access group with name '" + request.getName() + "' already exists");
        }

        accessGroup.setName(request.getName());
        accessGroup.setDescription(request.getDescription());
        accessGroup.setType(request.getType());

        updateRoleMappings(accessGroup, request.getRoleGroupMaps());

        AccessGroup updatedAccessGroup = accessGroupRepository.save(accessGroup);
        return accessGroupMapper.mapToResponseDto(updatedAccessGroup);
    }

    @Override
    public void deleteAccessGroup(Long id) {
        AccessGroup accessGroup = accessGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Access group not found with id: " + id));

        Status deletedStatus = statusRepository.findByName("DELETE")
                .orElseThrow(() -> new RuntimeException("Delete status not found"));

        accessGroup.setStatus(deletedStatus);
        accessGroupRepository.save(accessGroup);
    }

    private void createRoleMappings(AccessGroup accessGroup, List<RoleGroupMapDto> roleGroupMaps) {
        for (RoleGroupMapDto roleMapDto : roleGroupMaps) {
            if (Boolean.TRUE.equals(roleMapDto.getIsActive())) {
                Role role = roleRepository.findById(roleMapDto.getMapId())
                        .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleMapDto.getMapId()));

                AccessGroupRoleMap roleMap = new AccessGroupRoleMap();
                roleMap.setAccessGroup(accessGroup);
                roleMap.setRole(role);
                roleMap.setIsActive(true);
                accessGroupRoleMapRepository.save(roleMap);
            }
        }
    }

    private void updateRoleMappings(AccessGroup accessGroup, List<RoleGroupMapDto> roleGroupMaps) {
        // First, deactivate all existing mappings
        accessGroupRoleMapRepository.deactivateAllMappingsForAccessGroup(accessGroup);

        // Then create/activate mappings from the request
        if (roleGroupMaps != null) {
            createRoleMappings(accessGroup, roleGroupMaps);
        }
    }
}
