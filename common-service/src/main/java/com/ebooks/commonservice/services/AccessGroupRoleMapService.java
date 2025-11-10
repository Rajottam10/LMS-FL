package com.ebooks.commonservice.services;

import com.ebooks.commonservice.entities.AccessGroup;
import com.ebooks.commonservice.entities.Role;
import java.util.List;

public interface AccessGroupRoleMapService {
    List<Role> getActiveRolesForAccessGroup(AccessGroup accessGroup);
}
