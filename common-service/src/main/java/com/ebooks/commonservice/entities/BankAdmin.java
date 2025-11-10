package com.ebooks.commonservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.Collection;
import java.util.List;
import lombok.Generated;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(
        name = "bank_admin"
)
public class BankAdmin implements UserDetails {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    private String fullName;
    private String email;
    private String password;
    @Column(
            unique = true,
            nullable = false
    )
    private String username;
    @ManyToOne
    @JoinColumn(
            name = "status_id"
    )
    private Status status;
    @OneToOne
    @JoinColumn(
            name = "bank_id"
    )
    private Bank bank;
    @ManyToOne
    @JoinColumn(
            name = "access_group_id"
    )
    private AccessGroup accessGroup;
    @Transient
    private Collection<? extends GrantedAuthority> authorities = List.of();

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public String getFullName() {
        return this.fullName;
    }

    @Generated
    public String getEmail() {
        return this.email;
    }

    @Generated
    public String getPassword() {
        return this.password;
    }

    @Generated
    public String getUsername() {
        return this.username;
    }

    @Generated
    public Status getStatus() {
        return this.status;
    }

    @Generated
    public Bank getBank() {
        return this.bank;
    }

    @Generated
    public AccessGroup getAccessGroup() {
        return this.accessGroup;
    }

    @Generated
    public void setId(final Long id) {
        this.id = id;
    }

    @Generated
    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    @Generated
    public void setEmail(final String email) {
        this.email = email;
    }

    @Generated
    public void setPassword(final String password) {
        this.password = password;
    }

    @Generated
    public void setUsername(final String username) {
        this.username = username;
    }

    @Generated
    public void setStatus(final Status status) {
        this.status = status;
    }

    @Generated
    public void setBank(final Bank bank) {
        this.bank = bank;
    }

    @Generated
    public void setAccessGroup(final AccessGroup accessGroup) {
        this.accessGroup = accessGroup;
    }

    @Generated
    public void setAuthorities(final Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Generated
    public BankAdmin(final Long id, final String fullName, final String email, final String password, final String username, final Status status, final Bank bank, final AccessGroup accessGroup, final Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.username = username;
        this.status = status;
        this.bank = bank;
        this.accessGroup = accessGroup;
        this.authorities = authorities;
    }

    @Generated
    public BankAdmin() {
    }
}
