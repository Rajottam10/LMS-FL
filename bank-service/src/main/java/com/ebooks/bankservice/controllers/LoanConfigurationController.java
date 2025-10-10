package com.ebooks.bankservice.controllers;

import com.ebooks.bankservice.dtos.LoanConfigRequestDto;
import com.ebooks.bankservice.dtos.LoanConfigResponseDto;
import com.ebooks.bankservice.services.LoanConfigurationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loan-configs")
public class LoanConfigurationController {

    @Autowired
    private LoanConfigurationService loanConfigurationService;

    @PostMapping
    @PreAuthorize("hasRole('LOAN_CONFIGURATION')")
    public ResponseEntity<LoanConfigResponseDto> createLoanConfiguration(
            @Valid @RequestBody LoanConfigRequestDto requestDto) {
        LoanConfigResponseDto response = loanConfigurationService.createLoanConfiguration(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/current")
    @PreAuthorize("hasAnyRole('LOAN_CONFIGURATION', 'BANK_ADMIN', 'BANK_USER')")
    public ResponseEntity<LoanConfigResponseDto> getCurrentBankLoanConfiguration() {
        LoanConfigResponseDto response = loanConfigurationService.getCurrentBankLoanConfiguration();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('LOAN_CONFIGURATION', 'BANK_ADMIN')")
    public ResponseEntity<List<LoanConfigResponseDto>> getBankLoanConfigurations() {
        List<LoanConfigResponseDto> responses = loanConfigurationService.getBankLoanConfigurations();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{configId}")
    @PreAuthorize("hasRole('LOAN_CONFIGURATION')")
    public ResponseEntity<LoanConfigResponseDto> updateLoanConfiguration(
            @PathVariable Long configId,
            @Valid @RequestBody LoanConfigRequestDto requestDto) {
        LoanConfigResponseDto response = loanConfigurationService.updateLoanConfiguration(configId, requestDto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{configId}/deactivate")
    @PreAuthorize("hasRole('LOAN_CONFIGURATION')")
    public ResponseEntity<Void> deactivateLoanConfiguration(@PathVariable Long configId) {
        loanConfigurationService.deactivateLoanConfiguration(configId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{configId}/activate")
    @PreAuthorize("hasRole('LOAN_CONFIGURATION')")
    public ResponseEntity<Void> activateLoanConfiguration(@PathVariable Long configId) {
        loanConfigurationService.activateLoanConfiguration(configId);
        return ResponseEntity.ok().build();
    }
}
