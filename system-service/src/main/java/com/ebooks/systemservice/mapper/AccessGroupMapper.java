package com.ebooks.systemservice.mapper;

import com.ebooks.commonservice.dtos.AccessGroupResponseDto;
import com.ebooks.commonservice.dtos.RoleDto;
import com.ebooks.commonservice.entities.AccessGroup;
import com.ebooks.commonservice.entities.AccessGroupRoleMap;
import com.ebooks.commonservice.entities.Role;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccessGroupMapper {

    public AccessGroupResponseDto mapToResponseDto(AccessGroup accessGroup) {
        AccessGroupResponseDto dto = new AccessGroupResponseDto();
        dto.setId(accessGroup.getId());
        dto.setName(accessGroup.getName());
        dto.setDescription(accessGroup.getDescription());
        dto.setType(accessGroup.getType());
        dto.setStatus(accessGroup.getStatus().getName());
        dto.setCreatedDate(accessGroup.getCreatedDate());
        dto.setUpdatedDate(accessGroup.getUpdatedDate());

        List<RoleDto> roleDtos = accessGroup.getAccessGroupRoleMaps().stream()
                .filter(map -> Boolean.TRUE.equals(map.getIsActive()))
                .map(this::mapToRoleDto)
                .collect(Collectors.toList());

        dto.setRoles(roleDtos);
        return dto;
    }

    private RoleDto mapToRoleDto(AccessGroupRoleMap roleMap) {
        RoleDto roleDto = new RoleDto();
        Role role = roleMap.getRole();
        roleDto.setId(role.getId());
        roleDto.setName(role.getName());
        roleDto.setPermission(role.getPermission());
        roleDto.setDescription(role.getDescription());
        roleDto.setIsActive(true);
        return roleDto;
    }
}