package com.ebooks.commonservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Generated;

public class AccessGroupRequest {
    private @NotBlank String name;
    private @NotBlank String description;
    private @NotBlank String type;
    private @NotNull List<RoleMappingRequest> roleGroupMaps;

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public String getDescription() {
        return this.description;
    }

    @Generated
    public String getType() {
        return this.type;
    }

    @Generated
    public List<RoleMappingRequest> getRoleGroupMaps() {
        return this.roleGroupMaps;
    }

    @Generated
    public void setName(final String name) {
        this.name = name;
    }

    @Generated
    public void setDescription(final String description) {
        this.description = description;
    }

    @Generated
    public void setType(final String type) {
        this.type = type;
    }

    @Generated
    public void setRoleGroupMaps(final List<RoleMappingRequest> roleGroupMaps) {
        this.roleGroupMaps = roleGroupMaps;
    }

    public static class RoleMappingRequest {
        private @NotNull Long roleId;
        private @NotNull Boolean isActive;

        @Generated
        public Long getRoleId() {
            return this.roleId;
        }

        @Generated
        public Boolean getIsActive() {
            return this.isActive;
        }

        @Generated
        public void setRoleId(final Long roleId) {
            this.roleId = roleId;
        }

        @Generated
        public void setIsActive(final Boolean isActive) {
            this.isActive = isActive;
        }
    }
}
