

package com.ebooks.systemservice.services.implementation;

import com.ebooks.commonservice.entities.Role;
import com.ebooks.commonservice.services.AccessGroupRoleMapService;
import com.ebooks.systemservice.entities.SystemUser;
import com.ebooks.systemservice.repositories.SystemUserRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Generated;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final SystemUserRepository systemUserRepository;
    private final AccessGroupRoleMapService accessGroupRoleMapService;

    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemUser systemUser = this.systemUserRepository.findByUsername(username);
        if (systemUser == null) {
            throw new UsernameNotFoundException("User not found with provided information.");
        } else {
            Collection<? extends GrantedAuthority> authorities = this.getAuthoritiesForSystemUser(systemUser);
            return new User(systemUser.getUsername(), systemUser.getPassword(), authorities);
        }
    }

    public Collection<? extends GrantedAuthority> getAuthoritiesForSystemUser(SystemUser systemUser) {
        List<GrantedAuthority> authorities = new ArrayList();
        if (systemUser.getAccessGroup() != null) {
            for(Role role : this.accessGroupRoleMapService.getActiveRolesForAccessGroup(systemUser.getAccessGroup())) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            }
        }

        return authorities;
    }

    @Generated
    public UserDetailsServiceImpl(final SystemUserRepository systemUserRepository, final AccessGroupRoleMapService accessGroupRoleMapService) {
        this.systemUserRepository = systemUserRepository;
        this.accessGroupRoleMapService = accessGroupRoleMapService;
    }
}
