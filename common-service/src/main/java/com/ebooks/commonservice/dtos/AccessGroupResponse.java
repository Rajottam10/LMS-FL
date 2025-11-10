package com.ebooks.commonservice.dtos;

import java.util.List;
import lombok.Generated;

public class AccessGroupResponse {
    private Long id;
    private String name;
    private String description;
    private String type;
    private List<RoleResponse> roles;

    @Generated
    public Long getId() {
        return this.id;
    }

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
    public List<RoleResponse> getRoles() {
        return this.roles;
    }

    @Generated
    public void setId(final Long id) {
        this.id = id;
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
    public void setRoles(final List<RoleResponse> roles) {
        this.roles = roles;
    }

    public static class RoleResponse {
        private String name;
        private String type;

        @Generated
        public String getName() {
            return this.name;
        }

        @Generated
        public String getType() {
            return this.type;
        }

        @Generated
        public void setName(final String name) {
            this.name = name;
        }

        @Generated
        public void setType(final String type) {
            this.type = type;
        }
    }
}
