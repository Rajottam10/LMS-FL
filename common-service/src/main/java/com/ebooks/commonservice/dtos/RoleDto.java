package com.ebooks.commonservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDto {
    private Long id;
    private String name;
    private String permission;
    private String description;
    private Boolean isActive;
}