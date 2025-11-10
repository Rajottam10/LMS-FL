package com.ebooks.bankservice.controllers;

import com.ebooks.bankservice.entities.BankUser;
import com.ebooks.bankservice.services.AccessGroupService;
import com.ebooks.commonservice.dtos.AccessGroupRequest;
import com.ebooks.commonservice.dtos.AccessGroupResponse;
import com.ebooks.commonservice.entities.Bank;
import com.ebooks.commonservice.entities.BankAdmin;
import com.ebooks.commonservice.entities.Role;
import com.ebooks.commonservice.services.RoleService;
import jakarta.validation.Valid;
import java.io.PrintStream;
import java.util.List;
import lombok.Generated;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/access-groups"})
@PreAuthorize("hasAnyRole('ACCESS_GROUP')")
public class AccessGroupController {
    private final RoleService roleService;
    private final AccessGroupService accessGroupService;

    @GetMapping({"roles"})
    public ResponseEntity<List<Role>> allAvailableRoles(@AuthenticationPrincipal BankAdmin bankAdmin) {
        PrintStream var10000 = System.out;
        String var10001 = String.valueOf(bankAdmin);
        var10000.println("BANK ADMIN - " + var10001 + "Type " + String.valueOf(bankAdmin.getClass()));
        List<Role> roles = this.roleService.getRolesForBankAdmin(bankAdmin);
        return ResponseEntity.ok(roles);
    }

    @GetMapping
    public ResponseEntity<List<AccessGroupResponse>> getAllAccessGroup(@AuthenticationPrincipal Object principal) {
        Bank bank;
        if (principal instanceof BankAdmin admin) {
            bank = admin.getBank();
        } else {
            if (!(principal instanceof BankUser)) {
                throw new RuntimeException("Invalid principal type");
            }

            BankUser user = (BankUser)principal;
            bank = user.getBank();
        }

        List<AccessGroupResponse> accessGroups = this.accessGroupService.getAllAccessGroups(bank);
        return ResponseEntity.ok(accessGroups);
    }

    @PostMapping
    public ResponseEntity<?> createAccessGroup(@RequestBody @Valid AccessGroupRequest request, @AuthenticationPrincipal Object principal) {
        return ResponseEntity.ok(this.accessGroupService.createAccessGroup(request, principal));
    }

    @GetMapping({"{accessGroupId}"})
    public ResponseEntity<AccessGroupResponse> getAccessGroupDetails(@PathVariable Long accessGroupId, @AuthenticationPrincipal Object principal) {
        Bank bank;
        if (principal instanceof BankAdmin admin) {
            bank = admin.getBank();
        } else {
            if (!(principal instanceof BankUser)) {
                throw new RuntimeException("Invalid principal type");
            }

            BankUser user = (BankUser)principal;
            bank = user.getBank();
        }

        AccessGroupResponse response = this.accessGroupService.getAccessGroupById(accessGroupId, bank);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping({"{accessGroupId}"})
    public ResponseEntity<Void> deleteAccessGroup(@PathVariable Long accessGroupId, @AuthenticationPrincipal Object principal) {
        Bank bank;
        if (principal instanceof BankAdmin admin) {
            bank = admin.getBank();
        } else {
            if (!(principal instanceof BankUser)) {
                throw new RuntimeException("Invalid principal type");
            }

            BankUser user = (BankUser)principal;
            bank = user.getBank();
        }

        this.accessGroupService.deleteAccessGroup(accessGroupId, bank);
        return ResponseEntity.noContent().build();
    }

    @PutMapping({"{accessGroupId}"})
    public ResponseEntity<AccessGroupResponse> updateAccessGroup(@PathVariable Long accessGroupId, @AuthenticationPrincipal Object principal, @RequestBody AccessGroupRequest accessGroupRequest) {
        Bank bank;
        if (principal instanceof BankAdmin admin) {
            bank = admin.getBank();
        } else {
            if (!(principal instanceof BankUser)) {
                throw new RuntimeException("Invalid principal type");
            }

            BankUser user = (BankUser)principal;
            bank = user.getBank();
        }

        AccessGroupResponse updatedAccessGroup = this.accessGroupService.updateAccessGroup(accessGroupRequest, accessGroupId, bank);
        return ResponseEntity.ok(updatedAccessGroup);
    }

    @Generated
    public AccessGroupController(final RoleService roleService, final AccessGroupService accessGroupService) {
        this.roleService = roleService;
        this.accessGroupService = accessGroupService;
    }
}

