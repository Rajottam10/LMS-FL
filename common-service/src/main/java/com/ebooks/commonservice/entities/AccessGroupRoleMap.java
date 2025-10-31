package com.ebooks.commonservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "access_group_role_map")
public class AccessGroupRoleMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "access_group_id")
    private AccessGroup accessGroup;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
    private Boolean isActive;
}
