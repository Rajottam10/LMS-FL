//package com.ebooks.loandisbursementservice.services.impl;
//
//import com.ebooks.loandisbursementservice.clients.BankISORestClient;
//import com.ebooks.loandisbursementservice.dtos.*;
//import com.ebooks.loandisbursementservice.entities.LoanDetail;
//import com.ebooks.loandisbursementservice.entities.EmiSchedule;
//import com.ebooks.loandisbursementservice.mapper.LoanConfirmMapper;
//import com.ebooks.loandisbursementservice.repositories.LoanDetailRepository;
//import com.ebooks.loandisbursementservice.repositories.EmiScheduleRepository;
//import com.ebooks.loandisbursementservice.services.LoanConfirmService;
//import com.ebooks.loandisbursementservice.services.EMICalculatorService;
//import com.ebooks.loandisbursementservice.util.LoanNumberGenerator;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class LoanConfirmServiceImpl implements LoanConfirmService {
//
//    private final LoanDetailRepository loanDetailRepository;
//    private final EmiScheduleRepository emiScheduleRepository;
//    private final EMICalculatorService emiCalculatorService;
//    private final BankISORestClient bankISORestClient;
//    private final LoanNumberGenerator loanNumberGenerator;
//
//    @Override
//    @Transactional
//    public LoanConfirmResponseDto confirmLoan(LoanConfirmRequestDto request) {
//        List<LoanDetail> pendingLoans = loanDetailRepository
//                .findByCustomerNumberAndBankCodeAndTenureAndStatus(
//                        request.getCustomerNumber(),
//                        request.getBankCode(),
//                        request.getTenure(),
//                        "PENDING"
//                );
//
//        Optional<LoanDetail> matchingLoan = pendingLoans.stream()
//                .filter(loan -> loan.getLoanAmount().compareTo(request.getLoanAmount()) == 0)
//                .findFirst();
//
//        if (matchingLoan.isEmpty()) {
//            throw new RuntimeException("No pending loan found for the provided details");
//        }
//
//        LoanDetail loanDetail = matchingLoan.get();
//
//        validateOTP(loanDetail, request.getOtp());
//
//        String loanNumber = loanNumberGenerator.generate20DigitLoanNumber(request.getBankCode());
//
//        loanDetail.setLoanNumber(loanNumber);
//        loanDetail.setStatus("CONFIRMED");
//        loanDetail.setOtp(null);
//        loanDetail.setUpdatedDate(LocalDateTime.now());
//
//        LoanDetail confirmedLoan = loanDetailRepository.save(loanDetail);
//
//        List<EmiSchedule> emiSchedules = emiCalculatorService.generateEMISchedules(
//                loanNumber,
//                request.getLoanAmount(),
//                request.getTenure(),
//                request.getPaymentDate(),
//                request.getBankCode()
//        );
//        emiScheduleRepository.saveAll(emiSchedules);
//
//        try {
//            processFundTransfer(confirmedLoan);
//            log.info("Loan confirmation and fund transfer completed for: {}", loanNumber);
//        } catch (Exception e) {
//            confirmedLoan.setStatus("FUND_TRANSFER_FAILED");
//            loanDetailRepository.save(confirmedLoan);
//            log.error("❌ Fund transfer failed for loan: {}", loanNumber, e);
//            throw new RuntimeException("Loan confirmation failed: " + e.getMessage());
//        }
//
//        // Step 7: Prepare and return response
//        return LoanConfirmMapper.toDto(confirmedLoan, emiSchedules, "Loan confirmed and funds transferred successfully");
//    }
//
//    private void validateOTP(LoanDetail loanDetail, String inputOTP) {
//        if (!"1234".equals(inputOTP)) {
//            throw new RuntimeException("Invalid OTP");
//        }
//
////        if (loanDetail.getOtpExpiry() != null && LocalDateTime.now().isAfter(loanDetail.getOtpExpiry())) {
////            throw new RuntimeException("OTP has expired");
////        }
//
//        if (!"PENDING".equals(loanDetail.getStatus())) {
//            throw new RuntimeException("Loan is not in pending state for OTP validation");
//        }
//    }
//
//    private void processFundTransfer(LoanDetail loanDetail) {
//        BankTransferRequestDto transferRequest = createFundTransferRequest(loanDetail);
//        BankTransferResponseDto transferResponse = bankISORestClient.processFundTransfer(transferRequest);
//
//        if (!"SUCCESS".equals(transferResponse.getStatus())) {
//            throw new RuntimeException("Fund transfer failed: " + transferResponse.getMessage());
//        }
//
//        // Update loan status to indicate successful fund transfer
//        loanDetail.setStatus("FUNDS_TRANSFERRED");
//        loanDetailRepository.save(loanDetail);
//
//        log.info("Fund transfer completed for loan: {}, reference: {}",
//                loanDetail.getLoanNumber(), transferResponse.getReferenceNumber());
//    }
//
//    private BankTransferRequestDto createFundTransferRequest(LoanDetail loanDetail) {
//        BankTransferRequestDto request = new BankTransferRequestDto();
//        request.setLoanNumber(loanDetail.getLoanNumber());
//        request.setTransactionId("TXN-" + System.currentTimeMillis());
//
//        List<TransactionDetailDto> transactions = List.of(
//                createTransactionDetail("BANK_MAIN_ACCOUNT", "DEBIT", loanDetail.getLoanAmount(),
//                        "Loan Disbursement - " + loanDetail.getLoanNumber(), LocalDate.now()),
//                createTransactionDetail("CUST_" + loanDetail.getCustomerNumber(), "CREDIT", loanDetail.getLoanAmount(),
//                        "Loan Receipt - " + loanDetail.getLoanNumber(), LocalDate.now())
//        );
//
//        request.setTransactionDetails(transactions);
//        return request;
//    }
//
//    private TransactionDetailDto createTransactionDetail(String accountNumber, String type,
//                                                         BigDecimal amount, String remarks, LocalDate valueDate) {
//        TransactionDetailDto transaction = new TransactionDetailDto();
//        transaction.setAccountNumber(accountNumber);
//        transaction.setTransactionType(type);
//        transaction.setAmount(amount);
//        transaction.setParticularRemarks(remarks);
//        transaction.setValueDate(valueDate);
//        return transaction;
//    }
//}