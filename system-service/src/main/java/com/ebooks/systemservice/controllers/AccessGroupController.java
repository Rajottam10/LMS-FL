package com.ebooks.systemservice.controllers;

import com.ebooks.commonservice.dtos.AccessGroupRequestDto;
import com.ebooks.commonservice.dtos.AccessGroupResponseDto;
import com.ebooks.systemservice.services.AccessGroupService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/access-groups")
public class AccessGroupController {
    private final AccessGroupService accessGroupService;

    public AccessGroupController(AccessGroupService accessGroupService){
        this.accessGroupService = accessGroupService;
    }

    @PostMapping
    @PreAuthorize("hasRole('GOD')")
    public ResponseEntity<AccessGroupResponseDto> createAccessGroup(@Valid @RequestBody AccessGroupRequestDto request){
        AccessGroupResponseDto response = accessGroupService.createAccessGroup(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('GOD')")
    public ResponseEntity<List<AccessGroupResponseDto>> getAllAccessGroups() {
        List<AccessGroupResponseDto> accessGroups = accessGroupService.getAllAccessGroup();
        return ResponseEntity.ok(accessGroups);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GOD')")
    public ResponseEntity<AccessGroupResponseDto> getAccessGroupById(@Valid @PathVariable Long id){
        AccessGroupResponseDto response = accessGroupService.getAccessGroupById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GOD')")
    public ResponseEntity<AccessGroupResponseDto> updateAccessGroup(
            @PathVariable Long id,
            @Valid @RequestBody AccessGroupRequestDto request) {
        AccessGroupResponseDto response = accessGroupService.updateAccessGroup(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GOD')")
    public ResponseEntity<Void> deleteAccessGroup(@PathVariable Long id) {
        accessGroupService.deleteAccessGroup(id);
        return ResponseEntity.ok().build();
    }
}
