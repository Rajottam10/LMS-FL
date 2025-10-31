package com.ebooks.loandisbursementservice.mapper;


import com.ebooks.loandisbursementservice.dtos.EligibilityResponse;
import com.ebooks.loandisbursementservice.entities.FoneloanLimits;
import java.util.List;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class EligibilityMapper {

    public EligibilityResponse foneloanLimitToEligibilityResponse(FoneloanLimits foneloanLimit) {
        if (foneloanLimit == null) {
            return null;
        }

        EligibilityResponse response = new EligibilityResponse();
        response.setCustomerNumber(foneloanLimit.getCustomerNumber());
        response.setCustomerName(foneloanLimit.getCustomerName());
        response.setBankCode(foneloanLimit.getBankCode());

        if (foneloanLimit.getOneMonthRecommendedLimit() != null) {
            response.setOneMonthRecommendedLimit(foneloanLimit.getOneMonthRecommendedLimit().doubleValue());
        }

        if (foneloanLimit.getEmiMonths() != null) {
            response.setEmiMonths(foneloanLimit.getEmiMonths());
        }

        if (foneloanLimit.getEmiMaxAmount() != null) {
            response.setEmiMaxAmount(foneloanLimit.getEmiMaxAmount().doubleValue());
        }

        return response;
    }

    public List<EligibilityResponse> foneloanLimitsToEligibilityResponses(List<FoneloanLimits> foneloanLimits) {
        if (foneloanLimits == null) {
            return null;
        }

        return foneloanLimits.stream()
                .map(this::foneloanLimitToEligibilityResponse)
                .collect(Collectors.toList());
    }
}


