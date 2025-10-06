package com.ebooks.systemservice.services.impl;

import com.ebooks.systemservice.entities.AccessGroupRoleMap;
import com.ebooks.systemservice.entities.SystemUser;
import com.ebooks.systemservice.repositories.AccessGroupRoleMapRepository;
import com.ebooks.systemservice.repositories.SystemUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SystemUserRepository userRepository;

    @Autowired
    private AccessGroupRoleMapRepository accessGroupRoleMapRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (user.getStatus() == null || !"ACTIVE".equals(user.getStatus().getName())) {
            throw new UsernameNotFoundException("User is not active: " + username);
        }

        List<GrantedAuthority> authorities = getUserAuthorities(user);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    private List<GrantedAuthority> getUserAuthorities(SystemUser user) {
        try {
            List<AccessGroupRoleMap> activeMaps = accessGroupRoleMapRepository
                    .findByAccessGroupAndIsActiveTrue(user.getAccessGroup());

            return activeMaps.stream()
                    .map(map -> new SimpleGrantedAuthority("ROLE_" + map.getRole().getPermission()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }
}