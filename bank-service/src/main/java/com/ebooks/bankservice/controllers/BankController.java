package com.ebooks.bankservice.controllers;

import com.ebooks.bankservice.dtos.BankCreationRequestDto;
import com.ebooks.bankservice.dtos.BankResponseDto;
import com.ebooks.bankservice.services.BankService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
