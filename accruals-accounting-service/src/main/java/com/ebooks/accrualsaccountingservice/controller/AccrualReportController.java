package com.ebooks.accrualsaccountingservice.controller;

import com.ebooks.accrualsaccountingservice.dtos.AccrualReportResponse;
import com.ebooks.accrualsaccountingservice.services.impl.AccrualReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accrual-report")
@RequiredArgsConstructor
public class AccrualReportController {

    private final AccrualReportService accrualReportService;

    @GetMapping
    public ResponseEntity<List<AccrualReportResponse>> getAccrualReport() {
        List<AccrualReportResponse> report = accrualReportService.getAccrualReport();
        return ResponseEntity.ok(report);
    }
}
