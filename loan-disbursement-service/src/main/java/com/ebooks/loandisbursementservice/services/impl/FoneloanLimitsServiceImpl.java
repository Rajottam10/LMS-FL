//package com.ebooks.loandisbursementservice.services.impl;
//
//
//import com.ebooks.loandisbursementservice.entities.FoneloanLimits;
//import com.ebooks.loandisbursementservice.mapper.FoneloanLimitsMapper;
//import com.ebooks.loandisbursementservice.services.FoneloanLimitsService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class FoneloanLimitsServiceImpl implements FoneloanLimitsService {
//
//    private final FoneloanLimitsRepository foneloanLimitsRepository;
//
//    @Override
//    public List<EligibilityResponseDto> getLoanEligibility(EligibilityRequestDto request) {
//        List<FoneloanLimits> limits = foneloanLimitsRepository.findByCustomerNumberAndBankCode(
//                request.getCustomerNumber(),
//                request.getBankCode()
//        );
//
//        return limits.stream()
//                .map(FoneloanLimitsMapper::toDto)
//                .collect(Collectors.toList());
//    }
//}
//
