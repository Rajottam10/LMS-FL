package com.ebooks.commonservice.services;

import com.ebooks.commonservice.entities.BankAdmin;
import com.ebooks.commonservice.entities.Role;
import java.util.List;

public interface RoleService {
    List<Role> getRolesByType(String type);

    List<Role> getRolesForBankAdmin(BankAdmin bankAdmin);
}
