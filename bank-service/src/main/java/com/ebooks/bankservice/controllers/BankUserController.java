package com.ebooks.bankservice.controllers;

import com.ebooks.bankservice.dtos.BankUserRequestDto;
import com.ebooks.bankservice.dtos.BankUserResponseDto;
import com.ebooks.bankservice.services.BankUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bank-users")
public class BankUserController {

    @Autowired
    private BankUserService bankUserService;

    @PostMapping
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<BankUserResponseDto> createBankUser(@Valid @RequestBody BankUserRequestDto requestDTO) {
        BankUserResponseDto response = bankUserService.createBankUser(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<List<BankUserResponseDto>> getBankUsers() {
        List<BankUserResponseDto> users = bankUserService.getBankUsersByCurrentBank();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/paginated")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<Page<BankUserResponseDto>> getBankUsersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Pageable pageable = (Pageable) PageRequest.of(page, size,
                direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());

        Page<BankUserResponseDto> usersPage = bankUserService.getBankUsersByCurrentBank(pageable);
        return ResponseEntity.ok(usersPage);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<BankUserResponseDto> getBankUser(@PathVariable Long userId) {
        BankUserResponseDto user = bankUserService.getBankUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<BankUserResponseDto> updateBankUser(
            @PathVariable Long userId,
            @Valid @RequestBody BankUserRequestDto requestDTO) {
        BankUserResponseDto updatedUser = bankUserService.updateBankUser(userId, requestDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<Void> deleteBankUser(@PathVariable Long userId) {
        bankUserService.deleteBankUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/block")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<Void> blockBankUser(@PathVariable Long userId) {
        bankUserService.blockBankUser(userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{userId}/unblock")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<Void> unblockBankUser(@PathVariable Long userId) {
        bankUserService.unblockBankUser(userId);
        return ResponseEntity.ok().build();
    }
}