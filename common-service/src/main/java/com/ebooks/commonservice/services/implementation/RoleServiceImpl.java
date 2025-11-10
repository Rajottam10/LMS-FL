package com.ebooks.commonservice.services.implementation;

import com.ebooks.commonservice.entities.AccessGroup;
import com.ebooks.commonservice.entities.BankAdmin;
import com.ebooks.commonservice.entities.Role;
import com.ebooks.commonservice.repositories.AccessGroupRoleMapRepository;
import com.ebooks.commonservice.repositories.BankAdminRepository;
import com.ebooks.commonservice.repositories.RoleRepository;
import com.ebooks.commonservice.services.RoleService;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Generated;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final AccessGroupRoleMapRepository accessGroupRoleMapRepository;
    private final BankAdminRepository bankAdminRepository;

    @Transactional(
            readOnly = true
    )
    public List<Role> getRolesByType(String type) {
        return this.roleRepository.findAllByType(type);
    }

    public List<Role> getRolesForBankAdmin(BankAdmin principal) {
        if (principal != null && principal.getId() != null) {
            BankAdmin admin = (BankAdmin)this.bankAdminRepository.findById(principal.getId()).orElse(principal);
            AccessGroup accessGroup = admin.getAccessGroup();
            if (accessGroup != null && accessGroup.getId() != null) {
                List<Long> roleIds = this.accessGroupRoleMapRepository.findActiveRoleIdsByAccessGroupIds(Collections.singletonList(accessGroup.getId()));
                if (roleIds != null && !roleIds.isEmpty()) {
                    List<Role> roles = this.roleRepository.findAllById(roleIds);
                    return (List)roles.stream().filter((r) -> "BANK".equalsIgnoreCase(r.getType())).collect(Collectors.toList());
                } else {
                    return Collections.emptyList();
                }
            } else {
                return Collections.emptyList();
            }
        } else {
            return Collections.emptyList();
        }
    }

    @Generated
    public RoleServiceImpl(final RoleRepository roleRepository, final AccessGroupRoleMapRepository accessGroupRoleMapRepository, final BankAdminRepository bankAdminRepository) {
        this.roleRepository = roleRepository;
        this.accessGroupRoleMapRepository = accessGroupRoleMapRepository;
        this.bankAdminRepository = bankAdminRepository;
    }
}
