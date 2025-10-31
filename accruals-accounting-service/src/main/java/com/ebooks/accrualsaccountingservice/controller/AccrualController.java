package com.ebooks.accrualsaccountingservice.controller;

import com.ebooks.accrualsaccountingservice.services.AccrualService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accruals")
public class AccrualController {

    private final AccrualService accrualService;

    public AccrualController(AccrualService accrualService) {
        this.accrualService = accrualService;
    }

    @PostMapping("/process")
    public ResponseEntity<String> processAccruals() {
        accrualService.processDailyAccruals();
        return ResponseEntity.ok("Daily accruals processed successfully.");
    }
}