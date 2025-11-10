package com.ebooks.systemservice.controllers;

import com.ebooks.systemservice.dtos.BankCreateRequest;
import com.ebooks.systemservice.dtos.BankResponse;
import com.ebooks.systemservice.dtos.BankUpdateRequest;
import com.ebooks.systemservice.services.BankService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.Generated;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/banks"})
public class BankController {
    private final BankService bankService;

    @PostMapping
    public ResponseEntity<BankResponse> createBank(@RequestBody @Valid BankCreateRequest bankCreateRequest) {
        return ResponseEntity.ok(this.bankService.createBankWithBankAdmin(bankCreateRequest));
    }

    @GetMapping
    public ResponseEntity<List<BankResponse>> getAllBanks() {
        List<BankResponse> responses = this.bankService.getAllBanksWithBankAdmin();
        return ResponseEntity.ok(responses);
    }

    @PutMapping({"{bankCode}"})
    public ResponseEntity<BankResponse> updateBank(@PathVariable String bankCode, @RequestBody @Valid BankUpdateRequest bankUpdateRequest) {
        BankResponse updatedBank = this.bankService.updateBankWithBankAdmin(bankCode, bankUpdateRequest);
        return ResponseEntity.ok(updatedBank);
    }

    @DeleteMapping({"{bankCode}"})
    public ResponseEntity<?> deleteBank(@PathVariable String bankCode) {
        this.bankService.deleteBankByBankCode(bankCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping({"{bankCode}"})
    public ResponseEntity<BankResponse> getBankDetails(@PathVariable String bankCode) {
        BankResponse bankResponse = this.bankService.findByBankCode(bankCode);
        return ResponseEntity.ok(bankResponse);
    }

    @Generated
    public BankController(final BankService bankService) {
        this.bankService = bankService;
    }
}
