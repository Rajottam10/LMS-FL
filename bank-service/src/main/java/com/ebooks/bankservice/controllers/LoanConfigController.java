package com.ebooks.bankservice.controllers;

import com.ebooks.bankservice.dtos.LoanConfigRequest;
import com.ebooks.bankservice.entities.BankUser;
import com.ebooks.bankservice.entities.LoanConfig;
import com.ebooks.bankservice.services.LoanConfigService;
import com.ebooks.commonservice.entities.Bank;
import com.ebooks.commonservice.entities.BankAdmin;
import jakarta.validation.Valid;
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
@RequestMapping({"/api/loan-config"})
@PreAuthorize("hasAnyRole('LOAN_CONFIGURATION')")
public class LoanConfigController {
    private final LoanConfigService loanConfigService;

    @PostMapping
    public ResponseEntity<LoanConfig> createLoanConfig(@AuthenticationPrincipal Object principal, @RequestBody LoanConfigRequest request) {
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

        LoanConfig loanConfig = this.loanConfigService.createLoanConfig(request, bank);
        return ResponseEntity.ok(loanConfig);
    }

    @GetMapping
    public ResponseEntity<List<LoanConfig>> getAllLoanConfigs(@AuthenticationPrincipal Object principal) {
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

        List<LoanConfig> loanConfigs = this.loanConfigService.getAllLoanConfigs(bank);
        return ResponseEntity.ok(loanConfigs);
    }

    @DeleteMapping({"{loanConfigId}"})
    public ResponseEntity<Void> deleteLoanConfig(@PathVariable Long loanConfigId, @AuthenticationPrincipal Object principal) {
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

        this.loanConfigService.deleteLoanConfig(loanConfigId, bank);
        return ResponseEntity.noContent().build();
    }

    @PutMapping({"{loanConfigId}"})
    public ResponseEntity<LoanConfig> updateLoanConfig(@PathVariable Long loanConfigId, @AuthenticationPrincipal Object principal, @RequestBody @Valid LoanConfigRequest loanConfigRequest) {
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

        LoanConfig updatedLoanConfig = this.loanConfigService.updateLoanConfig(loanConfigId, loanConfigRequest, bank);
        return ResponseEntity.ok(updatedLoanConfig);
    }

    @Generated
    public LoanConfigController(final LoanConfigService loanConfigService) {
        this.loanConfigService = loanConfigService;
    }
}

