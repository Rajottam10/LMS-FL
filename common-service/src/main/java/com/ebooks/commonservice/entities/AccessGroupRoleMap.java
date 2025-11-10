package com.ebooks.commonservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Generated;

@Entity
@Table(
        name = "access_group_role_map"
)
public class AccessGroupRoleMap {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    @ManyToOne
    @JoinColumn(
            name = "access_group_id"
    )
    private AccessGroup accessGroup;
    @ManyToOne
    @JoinColumn(
            name = "role_id"
    )
    private Role role;
    private Boolean isActive;

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public AccessGroup getAccessGroup() {
        return this.accessGroup;
    }

    @Generated
    public Role getRole() {
        return this.role;
    }

    @Generated
    public Boolean getIsActive() {
        return this.isActive;
    }

    @Generated
    public void setId(final Long id) {
        this.id = id;
    }

    @Generated
    public void setAccessGroup(final AccessGroup accessGroup) {
        this.accessGroup = accessGroup;
    }

    @Generated
    public void setRole(final Role role) {
        this.role = role;
    }

    @Generated
    public void setIsActive(final Boolean isActive) {
        this.isActive = isActive;
    }

    @Generated
    public AccessGroupRoleMap(final Long id, final AccessGroup accessGroup, final Role role, final Boolean isActive) {
        this.id = id;
        this.accessGroup = accessGroup;
        this.role = role;
        this.isActive = isActive;
    }

    @Generated
    public AccessGroupRoleMap() {
    }
}
