package com.ebooks.commonservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AccessGroupResponseDto {
    private Long id;
    private String name;
    private String description;
    private String type;
    private Long statusId;
    private String statusName;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private List<RoleDto> roles;
}