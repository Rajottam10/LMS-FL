package com.ebooks.systemservice.entities;

import com.ebooks.commonservice.entities.AccessGroup;
import com.ebooks.commonservice.entities.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import lombok.Generated;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(
        name = "system_user"
)
public class SystemUser implements UserDetails {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    private String fullName;
    private String email;
    @Column(
            unique = true,
            nullable = false
    )
    private String password;
    private String username;
    @ManyToOne
    @JoinColumn(
            name = "status_id"
    )
    private Status status;
    @ManyToOne
    @JoinColumn(
            name = "access_group_id"
    )
    private AccessGroup accessGroup;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList();
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

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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
    public AccessGroup getAccessGroup() {
        return this.accessGroup;
    }

    @Generated
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Generated
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
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
    public void setAccessGroup(final AccessGroup accessGroup) {
        this.accessGroup = accessGroup;
    }

    @Generated
    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Generated
    public void setUpdatedAt(final LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Generated
    public SystemUser(final Long id, final String fullName, final String email, final String password, final String username, final Status status, final AccessGroup accessGroup, final LocalDateTime createdAt, final LocalDateTime updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.username = username;
        this.status = status;
        this.accessGroup = accessGroup;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Generated
    public SystemUser() {
    }
}
