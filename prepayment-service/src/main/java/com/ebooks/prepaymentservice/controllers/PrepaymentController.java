package com.ebooks.prepaymentservice.controllers;

import com.ebooks.prepaymentservice.dtos.PrepaymentConfirmRequest;
import com.ebooks.prepaymentservice.dtos.PrepaymentInquiry;
import com.ebooks.prepaymentservice.services.PrepaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/prepayment")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrepaymentController {

    private final PrepaymentService prepaymentService;

    @GetMapping("/inquiry")
    public ResponseEntity<PrepaymentInquiry> inquiry(
            @RequestParam @NotBlank String loanNumber) {

        log.info("Prepayment inquiry for loan: {}", loanNumber);

        PrepaymentInquiry response = prepaymentService.inquiry(loanNumber);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirm(@Valid @RequestParam @NotBlank String loanNumber) {
        log.info("Prepayment confirm for loan: {}", loanNumber);

        String result = prepaymentService.confirmPrepayment(loanNumber);

        return ResponseEntity.ok(result);
    }
}