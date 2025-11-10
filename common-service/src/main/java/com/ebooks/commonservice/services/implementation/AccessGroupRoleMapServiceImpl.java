package com.ebooks.commonservice.services.implementation;

import com.ebooks.commonservice.entities.AccessGroup;
import com.ebooks.commonservice.entities.AccessGroupRoleMap;
import com.ebooks.commonservice.entities.Role;
import com.ebooks.commonservice.repositories.AccessGroupRoleMapRepository;
import com.ebooks.commonservice.services.AccessGroupRoleMapService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Generated;
import org.springframework.stereotype.Service;

@Service
public class AccessGroupRoleMapServiceImpl implements AccessGroupRoleMapService {
    private final AccessGroupRoleMapRepository accessGroupRoleMapRepository;

    public List<Role> getActiveRolesForAccessGroup(AccessGroup accessGroup) {
        List<AccessGroupRoleMap> roleMappings = this.accessGroupRoleMapRepository.findByAccessGroup(accessGroup);
        return (List)roleMappings.stream().filter(AccessGroupRoleMap::getIsActive).map(AccessGroupRoleMap::getRole).collect(Collectors.toList());
    }

    @Generated
    public AccessGroupRoleMapServiceImpl(final AccessGroupRoleMapRepository accessGroupRoleMapRepository) {
        this.accessGroupRoleMapRepository = accessGroupRoleMapRepository;
    }
}
