package com.ebooks.systemservice.controllers;

import com.ebooks.commonservice.dtos.AccessGroupRequest;
import com.ebooks.commonservice.dtos.AccessGroupResponse;
import com.ebooks.commonservice.entities.Role;
import com.ebooks.commonservice.services.RoleService;
import com.ebooks.systemservice.services.AccessGroupService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.Generated;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAnyRole('GOD')")
@RequestMapping({"/api/access-groups"})
public class AccessGroupController {
    private final RoleService roleService;
    private final AccessGroupService accessGroupService;

    @GetMapping({"/roles"})
    public ResponseEntity<List<Role>> getAllRoles(@RequestParam String type) {
        List<Role> roles = this.roleService.getRolesByType(type);
        return ResponseEntity.ok(roles);
    }

    @GetMapping
    public ResponseEntity<List<AccessGroupResponse>> getAllAccessGroups() {
        List<AccessGroupResponse> accessGroups = this.accessGroupService.getAllAccessGroup();
        return ResponseEntity.ok(accessGroups);
    }

    @PostMapping
    public ResponseEntity<AccessGroupResponse> createAccessGroup(@RequestBody @Valid AccessGroupRequest request) {
        AccessGroupResponse response = this.accessGroupService.createAccessGroup(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping({"{accessGroupId}"})
    public ResponseEntity<AccessGroupResponse> accessGroupDetails(@PathVariable Long accessGroupId) {
        AccessGroupResponse response = this.accessGroupService.findAccessGroupById(accessGroupId);
        return ResponseEntity.ok(response);
    }

    @PutMapping({"{accessGroupId}"})
    public ResponseEntity<AccessGroupResponse> updateAccessGroup(@PathVariable Long accessGroupId, @RequestBody AccessGroupRequest request) {
        AccessGroupResponse response = this.accessGroupService.updateAccessGroup(accessGroupId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping({"{accessGroupId}"})
    public ResponseEntity<?> deleteAccessGroup(@PathVariable Long accessGroupId) {
        this.accessGroupService.deleteAccessGroup(accessGroupId);
        return ResponseEntity.noContent().build();
    }

    @Generated
    public AccessGroupController(final RoleService roleService, final AccessGroupService accessGroupService) {
        this.roleService = roleService;
        this.accessGroupService = accessGroupService;
    }
}
