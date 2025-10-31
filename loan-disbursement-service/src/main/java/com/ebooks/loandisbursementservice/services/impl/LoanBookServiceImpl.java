//package com.ebooks.loandisbursementservice.services.impl;
//
//import com.ebooks.loandisbursementservice.entities.FoneloanLimits;
//import com.ebooks.loandisbursementservice.entities.LoanDetail;
//import com.ebooks.loandisbursementservice.entities.EmiSchedule;
//import com.ebooks.loandisbursementservice.mapper.LoanBookMapper;
//import com.ebooks.loandisbursementservice.repositories.EmiScheduleRepository;
//import com.ebooks.loandisbursementservice.repositories.LoanDetailRepository;
//import com.ebooks.loandisbursementservice.services.BankServiceClient;
//import com.ebooks.loandisbursementservice.services.EMICalculatorService;
//import com.ebooks.loandisbursementservice.services.LoanBookService;
//import com.ebooks.loandisbursementservice.util.LoanNumberGenerator;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class LoanBookServiceImpl implements LoanBookService {
//
//    private final LoanDetailRepository loanDetailRepository;
//    private final EmiScheduleRepository emiScheduleRepository;
//    private final FoneloanLimitsRepository foneloanLimitsRepository;
//    private final BankServiceClient bankServiceClient;
//    private final EMICalculatorService emiCalculatorService;
//    private final LoanNumberGenerator loanNumberGenerator;
//
//    @Override
//    @Transactional
//    public LoanBookResponseDto bookLoan(LoanBookRequestDto request) {
//        // Step 1: Validate against loan configuration
//        LoanConfigurationDto config = bankServiceClient.getLoanConfiguration(request.getBankCode());
//        validateLoanAmount(request.getLoanAmount(), config);
//
//        // Step 2: Validate against foneloan_limits
//        validateCustomerEligibility(request.getCustomerNumber(), request.getBankCode(), request.getTenure());
//
//        // Step 3: Calculate EMI, admin fee, etc.
//        BigDecimal emiAmount = emiCalculatorService.calculateEMI(request.getLoanAmount(), request.getTenure());
//        BigDecimal adminFee = emiCalculatorService.calculateAdminFee(request.getLoanAmount());
//        BigDecimal interestRate = emiCalculatorService.getInterestRate();
//
//        // Step 4: Generate loan number and save LoanDetail
//        String loanNumber = loanNumberGenerator.generate20DigitLoanNumber(request.getBankCode());
//
//        LoanDetail loanDetail = new LoanDetail();
//        loanDetail.setLoanNumber(loanNumber);
//        loanDetail.setCustomerNumber(request.getCustomerNumber());
//        loanDetail.setBankCode(request.getBankCode());
//        loanDetail.setLoanAmount(request.getLoanAmount());
//        loanDetail.setTenure(request.getTenure());
//        loanDetail.setPaymentDate(request.getPaymentDate());
//        loanDetail.setLoanAdminFee(adminFee);
//        loanDetail.setTotalEmiAmount(emiAmount);
//        loanDetail.setInterestRate(interestRate);
//        loanDetail.setOtp("1234"); // Dummy OTP
//        loanDetail.setStatus("PENDING");
//
//        LoanDetail savedLoan = loanDetailRepository.save(loanDetail);
//
//        // Step 5: Generate EMI schedules
//        List<EmiSchedule> emiSchedules = emiCalculatorService.generateEMISchedules(
//                loanNumber, request.getLoanAmount(), request.getTenure(), request.getPaymentDate(), request.getBankCode()
//        );
//        emiScheduleRepository.saveAll(emiSchedules);
//
//        // Step 6: Prepare and return response
//        return LoanBookMapper.toDto(savedLoan, emiSchedules);
//    }
//
//    private void validateLoanAmount(BigDecimal loanAmount, LoanConfigurationDto config) {
//        if (loanAmount.compareTo(config.getMinLoanAmount()) < 0 ||
//                loanAmount.compareTo(config.getMaxLoanAmount()) > 0) {
//            throw new RuntimeException("Loan amount must be between " + config.getMinLoanAmount() +
//                    " and " + config.getMaxLoanAmount());
//        }
//    }
//
//    private void validateCustomerEligibility(String customerNumber, String bankCode, Integer tenure) {
//        List<FoneloanLimits> limits = foneloanLimitsRepository.findByCustomerNumberAndBankCode(customerNumber, bankCode);
//
//        boolean tenureEligible = limits.stream()
//                .anyMatch(limit -> limit.getEmiMonths().equals(tenure));
//
//        if (!tenureEligible) {
//            throw new RuntimeException("Customer not eligible for the requested tenure: " + tenure);
//        }
//    }
//}
