package com.ebooks.bankservice.security;

import com.ebooks.bankservice.entities.BankUser;
import com.ebooks.bankservice.repositories.BankUserRepository;
import com.ebooks.commonservice.entities.AccessGroupRoleMap;
import com.ebooks.commonservice.repositories.AccessGroupRoleMapRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class BankUserDetailsServiceImpl implements UserDetailsService {

    private final BankUserRepository bankUserRepository;
    private final AccessGroupRoleMapRepository accessGroupRoleMapRepository;

    public BankUserDetailsServiceImpl(BankUserRepository bankUserRepository,
                                      AccessGroupRoleMapRepository accessGroupRoleMapRepository) {
        this.bankUserRepository = bankUserRepository;
        this.accessGroupRoleMapRepository = accessGroupRoleMapRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BankUser bankUser = bankUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Bank user not found with username: " + username));

        if (bankUser.getStatus() == null || !"ACTIVE".equals(bankUser.getStatus())) {
            throw new UsernameNotFoundException("Bank user is not active: " + username);
        }

        Collection<? extends GrantedAuthority> authorities = getAuthoritiesForBankUser(bankUser);

        return new org.springframework.security.core.userdetails.User(
                bankUser.getUsername(),
                bankUser.getPassword(),
                authorities
        );
    }

    private Collection<? extends GrantedAuthority> getAuthoritiesForBankUser(BankUser bankUser) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (bankUser.getAccessGroup() != null) {
            List<AccessGroupRoleMap> activeMaps = accessGroupRoleMapRepository
                    .findByAccessGroupAndIsActiveTrue(bankUser.getAccessGroup());

            for (AccessGroupRoleMap map : activeMaps) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + map.getRole().getPermission()));
            }
        }
        return authorities;
    }
}
