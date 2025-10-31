package com.ebooks.loandisbursementservice.controllers;

import com.ebooks.loandisbursementservice.dtos.EligibilityRequest;
import com.ebooks.loandisbursementservice.dtos.EligibilityResponse;
import com.ebooks.loandisbursementservice.services.EligibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loan")
@RequiredArgsConstructor
public class EligibilityController {

    private final EligibilityService eligibilityService;

    @PostMapping("/eligibility")
    public ResponseEntity<List<EligibilityResponse>> getEligibility(@RequestBody EligibilityRequest request) {
    List<EligibilityResponse> eligibilityList = eligibilityService.getEligibility(request);
        if (eligibilityList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(eligibilityList);
    }

}
