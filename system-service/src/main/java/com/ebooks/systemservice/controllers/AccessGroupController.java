package com.ebooks.systemservice.controllers;

import com.ebooks.commonservice.dtos.AccessGroupRequest;
import com.ebooks.commonservice.dtos.AccessGroupResponse;
import com.ebooks.commonservice.entities.Role;
import com.ebooks.commonservice.services.RoleService;
import com.ebooks.systemservice.services.AccessGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('GOD')")
@RequestMapping("/api/access-groups")
@RequiredArgsConstructor
public class AccessGroupController {
    private final RoleService roleService;
    private final AccessGroupService accessGroupService;

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles(@RequestParam String type){
        List<Role> roles = roleService.getRolesByType(type);
        return ResponseEntity.ok(roles);
    }

    @GetMapping
    public ResponseEntity<List<AccessGroupResponse>> getAllAccessGroups(){
        List<AccessGroupResponse> accessGroups = accessGroupService.getAllAccessGroup();
        return ResponseEntity.ok(accessGroups);
    }

    @PostMapping
    public ResponseEntity<AccessGroupResponse> createAccessGroup(@RequestBody @Valid AccessGroupRequest request){
        AccessGroupResponse response = accessGroupService.createAccessGroup(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("{accessGroupId}")
    public ResponseEntity<AccessGroupResponse> accessGroupDetails(@PathVariable Long accessGroupId){
        AccessGroupResponse response = accessGroupService.findAccessGroupById(accessGroupId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("{accessGroupId}")
    public ResponseEntity<AccessGroupResponse> updateAccessGroup(@PathVariable Long accessGroupId,
                                                                 @RequestBody AccessGroupRequest request){
        AccessGroupResponse response = accessGroupService.updateAccessGroup(accessGroupId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{accessGroupId}")
    public ResponseEntity<?> deleteAccessGroup(@PathVariable Long accessGroupId){
        accessGroupService.deleteAccessGroup(accessGroupId);
        return ResponseEntity.noContent().build();
    }
}
