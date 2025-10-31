package com.ebooks.loandisbursementservice.services;

import com.ebooks.loandisbursementservice.dtos.EligibilityRequest;
import com.ebooks.loandisbursementservice.dtos.EligibilityResponse;
import com.ebooks.loandisbursementservice.entities.FoneloanLimits;
import com.ebooks.loandisbursementservice.mapper.EligibilityMapper;
import com.ebooks.loandisbursementservice.repositories.FoneloanLimitsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EligibilityService {

    private final FoneloanLimitsRepository repository;
    private final EligibilityMapper eligibilityMapper;

    public List<EligibilityResponse> getEligibility(EligibilityRequest request) {
        List<FoneloanLimits> foneloanLimits =
                repository.findByCustomerNumberAndBankCode(request.getCustomerNumber(), request.getBankCode());
        return eligibilityMapper.foneloanLimitsToEligibilityResponses(foneloanLimits);
    }
}

