package com.ebooks.loandisbursementservice.controllers;

import com.ebooks.loandisbursementservice.dtos.LoanBookRequest;
import com.ebooks.loandisbursementservice.dtos.LoanBookResponse;
import com.ebooks.loandisbursementservice.dtos.LoanConfirmRequest;
import com.ebooks.loandisbursementservice.dtos.LoanConfirmResponse;
import com.ebooks.loandisbursementservice.dtos.LoanProcessRequest;
import com.ebooks.loandisbursementservice.dtos.LoanProcessResponse;
import com.ebooks.loandisbursementservice.services.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loan")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    @PostMapping("/process")
    public ResponseEntity<LoanProcessResponse> processLoan(@RequestBody LoanProcessRequest request) {
        LoanProcessResponse response = loanService.processLoan(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/book")
    public ResponseEntity<LoanBookResponse> bookLoan(@RequestBody LoanBookRequest request) {
        LoanBookResponse response = loanService.bookLoan(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<LoanConfirmResponse> confirmLoan(@RequestBody LoanConfirmRequest request) {
        LoanConfirmResponse response = loanService.confirmLoan(request);
        return ResponseEntity.ok(response);
    }
}
