package com.ebooks.bankservice.services;

import com.ebooks.commonservice.entities.BankAdmin;
import com.ebooks.commonservice.entities.Role;
import com.ebooks.commonservice.repositories.BankAdminRepository;
import com.ebooks.commonservice.services.AccessGroupRoleMapService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BankAdminUserDetailsServiceImpl implements UserDetailsService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(BankAdminUserDetailsServiceImpl.class);
    private final BankAdminRepository bankAdminRepository;
    private final AccessGroupRoleMapService accessGroupRoleMapService;

    public BankAdmin loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading BankAdmin: {}", username);
        BankAdmin bankAdmin = this.bankAdminRepository.findByUsername(username);
        if (bankAdmin == null) {
            log.warn("BankAdmin not found: {}", username);
            throw new UsernameNotFoundException("BankAdmin not found with username: " + username);
        } else {
            Collection<? extends GrantedAuthority> authorities = this.getAuthoritiesForBankUser(bankAdmin);
            log.debug("BankAdmin {} authorities: {}", username, authorities);
            bankAdmin.setAuthorities(authorities);
            return bankAdmin;
        }
    }

    public Collection<? extends GrantedAuthority> getAuthoritiesForBankUser(BankAdmin bankAdmin) {
        List<GrantedAuthority> authorities = new ArrayList();
        if (bankAdmin.getAccessGroup() != null) {
            List<Role> roles = this.accessGroupRoleMapService.getActiveRolesForAccessGroup(bankAdmin.getAccessGroup());
            log.debug("Found {} roles for access group: {}", roles.size(), bankAdmin.getAccessGroup().getName());

            for(Role role : roles) {
                String authority = "ROLE_" + role.getName().toUpperCase();
                authorities.add(new SimpleGrantedAuthority(authority));
                log.debug("Added authority: {}", authority);
            }
        } else {
            log.warn("BankAdmin {} has no access group", bankAdmin.getUsername());
        }

        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            log.warn("No authorities found for BankAdmin {}, added default ROLE_USER", bankAdmin.getUsername());
        }

        return authorities;
    }

    @Generated
    public BankAdminUserDetailsServiceImpl(final BankAdminRepository bankAdminRepository, final AccessGroupRoleMapService accessGroupRoleMapService) {
        this.bankAdminRepository = bankAdminRepository;
        this.accessGroupRoleMapService = accessGroupRoleMapService;
    }
}
