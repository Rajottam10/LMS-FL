//package com.ebooks.loandisbursementservice.services.impl;
//
//import com.ebooks.loandisbursementservice.entities.FoneloanLimits;
//import com.ebooks.loandisbursementservice.mapper.BankLoanConfigMapper;
//import com.ebooks.loandisbursementservice.repositories.BankLoanConfigRepository;
//import com.ebooks.loandisbursementservice.services.LoanProcessService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class LoanProcessServiceImpl implements LoanProcessService {
//
//    private final FoneloanLimitsRepository foneloanLimitsRepository;
//    private final BankLoanConfigRepository bankLoanConfigRepository;
//    private final BankLoanConfigMapper bankLoanConfigMapper;
//
//    @Override
//    public LoanProcessResponseDto processLoan(LoanProcessRequestDto request) {
//
//        // Step 1: Validate customer eligibility
//        List<FoneloanLimits> limits = foneloanLimitsRepository.findByCustomerNumberAndBankCode(
//                request.getCustomerNumber(), request.getBankCode()
//        );
//
//        if (limits.isEmpty()) {
//            throw new RuntimeException("Customer not found or not eligible for the given bank");
//        }
//
//        // Step 2: Get loan configuration from Bank Service
//        LoanConfigurationDto config = bankLoanConfigMapper.loanConfigurationDto(
//                bankLoanConfigRepository.findByBankCode(request.getBankCode())
//                        .orElseThrow(() -> new RuntimeException("Bank configuration not found for bank code: " + request.getBankCode()))
//        );
//
//        // Step 3: Calculate eligible amounts for ALL tenures
//        List<EligibleTenureDto> eligibleTenures = limits.stream()
//                .map(limit -> {
//                    // Calculate potential loan amount: EMI capacity × tenure
//                    BigDecimal potentialLoanAmount = limit.getOneMonthRecommendedLimit()
//                            .multiply(BigDecimal.valueOf(limit.getEmiMonths()));
//
//                    // Apply bank min/max constraints
//                    BigDecimal eligibleAmount;
//                    if (potentialLoanAmount.compareTo(config.getMinLoanAmount()) < 0) {
//                        // Below minimum - not eligible
//                        eligibleAmount = BigDecimal.ZERO;
//                    } else if (potentialLoanAmount.compareTo(config.getMaxLoanAmount()) > 0) {
//                        // Above maximum - cap at maximum
//                        eligibleAmount = config.getMaxLoanAmount();
//                    } else {
//                        // Within range - use calculated amount
//                        eligibleAmount = potentialLoanAmount;
//                    }
//
//                    // Create tenure DTO
//                    return new EligibleTenureDto(limit.getEmiMonths(), eligibleAmount);
//                })
//                .filter(tenure -> tenure.getEligibleAmount().compareTo(BigDecimal.ZERO) > 0)
//                .collect(Collectors.toList());
//
//        if (eligibleTenures.isEmpty()) {
//            throw new RuntimeException("No eligible loans found for this customer within bank limits");
//        }
//
//        // Step 4: Prepare response
//        LoanProcessResponseDto response = new LoanProcessResponseDto();
//        response.setCustomerNumber(request.getCustomerNumber());
//        response.setBankCode(request.getBankCode());
//        response.setTenures(eligibleTenures);
//        return response;
//    }
//}