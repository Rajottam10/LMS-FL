package com.ebooks.bankservice.controllers;

import com.ebooks.bankservice.dtos.BankUserRequest;
import com.ebooks.bankservice.dtos.BankUserResponse;
import com.ebooks.bankservice.dtos.BankUserUpdateRequest;
import com.ebooks.bankservice.entities.BankUser;
import com.ebooks.bankservice.services.BankUserService;
import com.ebooks.commonservice.entities.Bank;
import com.ebooks.commonservice.entities.BankAdmin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class BankUserController {
    private final BankUserService bankUserService;

    @GetMapping
    @PreAuthorize("hasAnyRole('READ_USER')")
    public ResponseEntity<List<BankUserResponse>> getAllBankUsers(@AuthenticationPrincipal Object principal,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        Bank bank;
        if (principal instanceof BankAdmin admin) {
            bank = admin.getBank();
        } else if (principal instanceof BankUser user) {
            bank = user.getBank();
        } else {
            throw new RuntimeException("Invalid principal type");
        }

        List<BankUserResponse> bankUserResponses = bankUserService.getAllBankUsers(bank, page, size);
        return ResponseEntity.ok(bankUserResponses);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CREATE_USER')")
    public ResponseEntity<BankUserResponse> createBankUser(@AuthenticationPrincipal Object principal,
                                                           @RequestBody BankUserRequest bankUserRequest) {
        Bank bank;
        if (principal instanceof BankAdmin admin) {
            bank = admin.getBank();
        } else if (principal instanceof BankUser user) {
            bank = user.getBank();
        } else {
            throw new RuntimeException("Invalid principal type");
        }
        BankUserResponse bankUserResponse = bankUserService.createBankUser(bankUserRequest, bank);
        return ResponseEntity.ok(bankUserResponse);
    }

    @DeleteMapping("{bankUserId}")
    @PreAuthorize("hasAnyRole('DELETE_USER')")
    public ResponseEntity<Void> deleteBankUserById(@PathVariable Long bankUserId,
                                                   @AuthenticationPrincipal Object principal) {
        Bank bank;
        if (principal instanceof BankAdmin admin) {
            bank = admin.getBank();
        } else if (principal instanceof BankUser user) {
            bank = user.getBank();
        } else {
            throw new RuntimeException("Invalid principal type");
        }
        bankUserService.deleteBankUserById(bankUserId, bank);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{bankUserId}")
    @PreAuthorize("hasAnyRole('CREATE_USER')")
    public ResponseEntity<BankUserResponse> updateUser(@PathVariable Long bankUserId,
                                                       @AuthenticationPrincipal Object principal,
                                                       @RequestBody BankUserUpdateRequest request) {
        Bank bank;
        if (principal instanceof BankAdmin admin) {
            bank = admin.getBank();
        } else if (principal instanceof BankUser user) {
            bank = user.getBank();
        } else {
            throw new RuntimeException("Invalid principal type");
        }
        BankUserResponse bankUserResponse = bankUserService.updateBankUser(request, bankUserId, bank);
        return ResponseEntity.ok(bankUserResponse);
    }

    @GetMapping("{bankUserId}")
    @PreAuthorize("hasAnyRole('READ_USER')")
    public ResponseEntity<BankUserResponse> getBankUserById(@PathVariable Long bankUserId,
                                                            @AuthenticationPrincipal Object principal) {
        Bank bank;
        if (principal instanceof BankAdmin admin) {
            bank = admin.getBank();
        } else if (principal instanceof BankUser user) {
            bank = user.getBank();
        } else {
            throw new RuntimeException("Invalid principal type");
        }
        BankUserResponse bankUserResponse = bankUserService.getBankUserById(bankUserId, bank);
        return ResponseEntity.ok(bankUserResponse);
    }

    @PutMapping("/{userId}/block")
    @PreAuthorize("hasAnyRole('BLOCK_USER')")
    public ResponseEntity<Void> blockBankUser(@AuthenticationPrincipal Object principal,
                                              @PathVariable Long userId) {
        Bank bank;
        if (principal instanceof BankAdmin admin) {
            bank = admin.getBank();
        } else if (principal instanceof BankUser user) {
            bank = user.getBank();
        } else {
            throw new RuntimeException("Invalid principal type");
        }
        bankUserService.blockBankUser(userId, bank);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/unblock")
    @PreAuthorize("hasAnyRole('UNBLOCK_USER')")
    public ResponseEntity<Void> unblockBankUser(@AuthenticationPrincipal Object principal,
                                                @PathVariable Long userId) {
        Bank bank;
        if (principal instanceof BankAdmin admin) {
            bank = admin.getBank();
        } else if (principal instanceof BankUser user) {
            bank = user.getBank();
        } else {
            throw new RuntimeException("Invalid principal type");
        }
        bankUserService.unblockBankUser(userId, bank);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/delete")
    @PreAuthorize("hasAnyRole('DELETE_USER')")
    public ResponseEntity<Void> deleteBankUser(@AuthenticationPrincipal Object principal,
                                               @PathVariable Long userId) {
        Bank bank;
        if (principal instanceof BankAdmin admin) {
            bank = admin.getBank();
        } else if (principal instanceof BankUser user) {
            bank = user.getBank();
        } else {
            throw new RuntimeException("Invalid principal type");
        }
        bankUserService.deleteBankUser(userId, bank);
        return ResponseEntity.ok().build();
    }
}