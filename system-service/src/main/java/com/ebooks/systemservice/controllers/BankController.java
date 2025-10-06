package com.ebooks.systemservice.controllers;

import com.ebooks.systemservice.dtos.bank.BankCreationRequestDto;
import com.ebooks.systemservice.dtos.bank.BankResponseDto;
import com.ebooks.systemservice.services.BankService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banks")
public class BankController {

    @Autowired
    private BankService bankService;

    @PostMapping
    @PreAuthorize("hasRole('GOD')")
    public ResponseEntity<BankResponseDto> createBank(@Valid @RequestBody BankCreationRequestDto request) {
        BankResponseDto bank = bankService.createBank(request);
        return ResponseEntity.ok(bank);
    }

    @GetMapping
    @PreAuthorize("hasRole('GOD')")
    public ResponseEntity<List<BankResponseDto>> getAllBanks() {
        List<BankResponseDto> banks = bankService.getAllBanks();
        return ResponseEntity.ok(banks);
    }

    @GetMapping("/{bankCode}")
    @PreAuthorize("hasRole('GOD')")
    public ResponseEntity<BankResponseDto> getBankByCode(@PathVariable String bankCode) {
        BankResponseDto bank = bankService.getBankByCode(bankCode);
        return ResponseEntity.ok(bank);
    }

    @PutMapping("/{bankId}")
    @PreAuthorize("hasRole('GOD')")
    public ResponseEntity<BankResponseDto> updateBank(
            @PathVariable Long bankId,
            @Valid @RequestBody BankCreationRequestDto request) {
        BankResponseDto bank = bankService.updateBank(bankId, request);
        return ResponseEntity.ok(bank);
    }

    @DeleteMapping("/{bankId}")
    @PreAuthorize("hasRole('GOD')")
    public ResponseEntity<Void> deleteBank(@PathVariable Long bankId) {
        bankService.deleteBank(bankId);
        return ResponseEntity.ok().build();
    }
}
