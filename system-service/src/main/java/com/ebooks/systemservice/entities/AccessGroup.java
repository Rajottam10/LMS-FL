package com.ebooks.systemservice.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "access_group")
public class AccessGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "access_group_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Access group name is required")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "type", nullable = false)
    @NotBlank(message = "Access group type is required")
    private String type;

    @Column(name = "recorded_date")
    private LocalDateTime recordedDate;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status")
    @NotNull(message = "Status is required")
    private Status status;

    @OneToMany(mappedBy = "accessGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AccessGroupRoleMap> accessGroupRoleMaps = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        recordedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }

    public void addRole(Role role) {
        AccessGroupRoleMap map = new AccessGroupRoleMap();
        map.setAccessGroup(this);
        map.setRole(role);
        map.setIsActive(true);
        this.accessGroupRoleMaps.add(map);
    }

    public void removeRole(Role role) {
        this.accessGroupRoleMaps.removeIf(map -> map.getRole().equals(role));
    }

    public void clearRoles() {
        this.accessGroupRoleMaps.clear();
    }
}