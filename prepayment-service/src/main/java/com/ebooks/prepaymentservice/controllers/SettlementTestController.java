package com.ebooks.prepaymentservice.controllers;

import com.ebooks.prepaymentservice.jobs.SettlementScheduler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/settlement")
public class SettlementTestController {
    private final SettlementScheduler settlementScheduler;

    public SettlementTestController(SettlementScheduler settlementScheduler) {
        this.settlementScheduler = settlementScheduler;
    }

    @GetMapping
    public ResponseEntity<?> loanSettlementTry(){
        settlementScheduler.loanSettlementJob();
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
