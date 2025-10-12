package com.ebooks.commonservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AccessGroupRequestDto {
    private Long id;

    @NotBlank(message = "Access group name is required")
    private String name;

    private String description;

    @NotBlank(message = "Access group type is required")
    private String type;

    private List<RoleGroupMapDto> roleGroupMaps;
}