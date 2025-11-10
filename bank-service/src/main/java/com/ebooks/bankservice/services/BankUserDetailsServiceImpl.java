package com.ebooks.bankservice.services;

import com.ebooks.bankservice.entities.BankUser;
import com.ebooks.bankservice.repositories.BankUserRepository;
import com.ebooks.commonservice.entities.Role;
import com.ebooks.commonservice.services.AccessGroupRoleMapService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Generated;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BankUserDetailsServiceImpl implements UserDetailsService {
    private final BankUserRepository bankUserRepository;
    private final AccessGroupRoleMapService accessGroupRoleMapService;

    public BankUser loadUserByUsername(String username) throws UsernameNotFoundException {
        BankUser bankUser = this.bankUserRepository.findBankUserByEmail(username);
        if (bankUser == null) {
            throw new UsernameNotFoundException("User not found with provided information.");
        } else {
            Collection<? extends GrantedAuthority> authorities = this.getAuthoritiesForBankUser(bankUser);
            bankUser.setAuthorities(authorities);
            return bankUser;
        }
    }

    public Collection<? extends GrantedAuthority> getAuthoritiesForBankUser(BankUser bankUser) {
        List<GrantedAuthority> authorities = new ArrayList();
        if (bankUser.getAccessGroup() != null) {
            for(Role role : this.accessGroupRoleMapService.getActiveRolesForAccessGroup(bankUser.getAccessGroup())) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            }
        }

        return authorities;
    }

    @Generated
    public BankUserDetailsServiceImpl(final BankUserRepository bankUserRepository, final AccessGroupRoleMapService accessGroupRoleMapService) {
        this.bankUserRepository = bankUserRepository;
        this.accessGroupRoleMapService = accessGroupRoleMapService;
    }
}
