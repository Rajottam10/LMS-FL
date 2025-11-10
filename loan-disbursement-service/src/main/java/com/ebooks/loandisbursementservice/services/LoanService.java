package com.ebooks.loandisbursementservice.services;

import com.ebooks.commonmoduleloan.entities.EMISchedule;
import com.ebooks.commonmoduleloan.entities.LoanDetail;
import com.ebooks.loandisbursementservice.dtos.IsoTransferRequest;
import com.ebooks.loandisbursementservice.dtos.IsoTransferResponse;
import com.ebooks.loandisbursementservice.dtos.LoanBookRequest;
import com.ebooks.loandisbursementservice.dtos.LoanBookResponse;
import com.ebooks.loandisbursementservice.dtos.LoanConfirmRequest;
import com.ebooks.loandisbursementservice.dtos.LoanConfirmResponse;
import com.ebooks.loandisbursementservice.dtos.LoanProcessRequest;
import com.ebooks.loandisbursementservice.dtos.LoanProcessResponse;
import com.ebooks.loandisbursementservice.dtos.LoanSchedule;
import com.ebooks.loandisbursementservice.entities.BankLoanConfig;
import com.ebooks.loandisbursementservice.entities.FoneloanLimits;
import com.ebooks.loandisbursementservice.repositories.EMIScheduleRepository;
import com.ebooks.loandisbursementservice.repositories.FoneloanLimitsRepository;
import com.ebooks.loandisbursementservice.repositories.LoanDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanService {
    private final FoneloanLimitsRepository foneloanLimitsRepository;
    private final LoanDetailRepository loanDetailRepository;
    private final EMIScheduleRepository emiScheduleRepository;
    private final BankIsoClient bankIsoClient;
    private final BankLoanConfigService bankLoanConfigService;

    @Transactional(readOnly = true)
    public LoanProcessResponse processLoan(LoanProcessRequest request) {
        List<FoneloanLimits> limits = foneloanLimitsRepository.findByCustomerNumberAndBankCode(
                request.getCustomerNumber(), request.getBankCode());

        if (limits.isEmpty()) {
            throw new RuntimeException("Customer not found or not eligible for loan.");
        }

        BankLoanConfig loanConfig = bankLoanConfigService.getLoanConfigForBank(request.getBankCode());
        BigDecimal minLoan = loanConfig.getMinAmount();
        BigDecimal maxLoan = loanConfig.getMaxAmount();

        List<Integer> validTenures = limits.stream()
                .filter(l -> l.getEmiMaxAmount() != null)
                .filter(l -> {
                    BigDecimal emiMax = l.getEmiMaxAmount();
                    return emiMax.compareTo(minLoan) >= 0 && emiMax.compareTo(maxLoan) <= 0;
                })
                .map(FoneloanLimits::getEmiMonths)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        BigDecimal eligibleAmount = limits.stream()
                .map(FoneloanLimits::getEmiMaxAmount)
                .filter(Objects::nonNull)
                .filter(a -> a.compareTo(minLoan) >= 0 && a.compareTo(maxLoan) <= 0)
                .max(BigDecimal::compareTo)
                .orElse(minLoan);

        LoanProcessResponse loanProcessResponse = new LoanProcessResponse();
        loanProcessResponse.setEligibleLoanAmount(eligibleAmount);
        loanProcessResponse.setTenures(validTenures);
        return loanProcessResponse;
    }

    private static final Map<String, String> otpStore = new HashMap<>();
    private static final Map<String, Map<String, Object>> bookingStore = new HashMap<>();

    @Transactional(readOnly = true)
    public LoanBookResponse bookLoan(LoanBookRequest request) {
        List<FoneloanLimits> limits = foneloanLimitsRepository.findByCustomerNumberAndBankCode(request.getCustomerNumber(),
                request.getBankCode());
        if (limits.isEmpty()) {
            throw new RuntimeException("Customer not found or not eligible for loan.");
        }
        int tenure = request.getTenure();
        BigDecimal loanAmount = request.getLoanAmount();

        Optional<FoneloanLimits> limitForTenure = limits.stream()
                .filter(l -> l.getEmiMonths() == tenure)
                .findFirst();

        if (limitForTenure.isEmpty()) {
            throw new RuntimeException("Requested tenure not available for this customer");
        }

        BigDecimal maxAllowedForTenure = limitForTenure.get().getEmiMaxAmount();

        if (loanAmount.compareTo(maxAllowedForTenure) > 0) {
            throw new RuntimeException("Loan amount exceeds maximum allowed for " + tenure + " months: " + maxAllowedForTenure);
        }


        BankLoanConfig bankLoanConfig = bankLoanConfigService.getLoanConfigForBank(request.getBankCode());
        BigDecimal minLoan = bankLoanConfig.getMinAmount();
        BigDecimal maxLoan = bankLoanConfig.getMaxAmount();


        if (loanAmount.compareTo(minLoan) < 0 || loanAmount.compareTo(maxLoan) > 0) {
            throw new RuntimeException("Loan must be between " + minLoan + " and " + maxLoan);
        }

        BigDecimal adminFee = calculateAdminFee(loanAmount, bankLoanConfig);

        BigDecimal annualInterestRate = bankLoanConfig.getAnnualInterestRate();

        BigDecimal monthlyRate = annualInterestRate.divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);

        BigDecimal onePlusRPowerN = (BigDecimal.ONE.add(monthlyRate)).pow(tenure);
        BigDecimal numerator = loanAmount.multiply(monthlyRate).multiply(onePlusRPowerN);
        BigDecimal denominator = onePlusRPowerN.subtract(BigDecimal.ONE);
        BigDecimal emi = numerator.divide(denominator, 2, RoundingMode.HALF_UP);

        List<LoanSchedule> schedules = new ArrayList<>();

        BigDecimal outstanding = loanAmount;

        for (int i = 1; i <= tenure; i++) {
            LocalDate installmentStart = request.getEmiStartDate().plusMonths(i - 1);
            LocalDate dueDate = installmentStart.plusMonths(1).minusDays(1);
            LocalDate demandDate = installmentStart.plusMonths(1);
            long days = ChronoUnit.DAYS.between(installmentStart, demandDate);

            BigDecimal interest = outstanding.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principal = emi.subtract(interest).setScale(2, RoundingMode.HALF_UP);

            if (i == tenure) {
                principal = outstanding;
                emi = principal.add(interest).setScale(2, RoundingMode.HALF_UP);;
            }
            outstanding = outstanding.subtract(principal).setScale(2, RoundingMode.HALF_UP);

            LoanSchedule schedule = new LoanSchedule();
            schedule.setInstallmentNo(i);
            schedule.setEmiAmount(emi);
            schedule.setInterestComponent(interest);
            schedule.setPrincipalComponent(principal);
            schedule.setOutstandingPrincipal(outstanding.max(BigDecimal.ZERO));
            schedule.setInstallmentStartDate(installmentStart);
            schedule.setDemandDate(demandDate);
            schedule.setInterestApplicableDays((int) days);
            schedule.setStatus("PENDING");
            schedules.add(schedule);
        }

        String otp = "1234";
        String key = request.getCustomerNumber() + "|" + request.getBankCode();

        otpStore.put(key, otp);
        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("loanAmount", loanAmount);
        bookingData.put("tenure", tenure);
        bookingData.put("emiStartDate", request.getEmiStartDate());
        bookingData.put("paymentDate", request.getEmiStartDate().plusMonths(1));
        bookingData.put("adminFee", adminFee);
        bookingData.put("schedules", schedules);
        bookingStore.put(key, bookingData);

        LoanBookResponse response = new LoanBookResponse();
        response.setLoanAdminFee(adminFee);
        response.setSchedules(schedules);
        return response;
    }

    private BigDecimal calculateAdminFee(BigDecimal loanAmount, BankLoanConfig config) {
        BigDecimal adminFeeValue = config.getAdminFeeValue();
        BigDecimal percentageFee = loanAmount.multiply(adminFeeValue).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        switch (config.getAdminFeeType()) {
            case PERCENTAGE:
                return percentageFee;
            case FLAT_RATE:
                return adminFeeValue;
            case GREATER_AMONGST_FLAT_OR_PERCENTAGE:
                return percentageFee.max(percentageFee);
            default:
                return BigDecimal.ZERO;
        }
    }

    @Transactional
    public LoanConfirmResponse confirmLoan(LoanConfirmRequest request) {
        String key = request.getCustomerNumber() + "|" + request.getBankCode();

        String otp = otpStore.get(key);
        if (otp == null || !otp.equals(request.getOtp())) {
            throw new RuntimeException("Invalid otp");
        }

        Map<String, Object> booking = bookingStore.get(key);
        if (booking == null) {
            throw new RuntimeException("No loan booking found for this customer.");
        }

        BigDecimal loanAmount = (BigDecimal) booking.get("loanAmount");
        int tenure = (Integer) booking.get("tenure");
        LocalDate emiStartDate = (LocalDate) booking.get("emiStartDate");
        BigDecimal adminFee = (BigDecimal) booking.get("adminFee");
        LocalDate paymentDate = (LocalDate) booking.get("paymentDate");

        @SuppressWarnings("unchecked")
        List<LoanSchedule> schedules = (List<LoanSchedule>) booking.get("schedules");

        String loanNumber = generateLoanNumber();

        LoanDetail loanDetail = LoanDetail.builder()
                .loanNumber(loanNumber)
                .customerNumber(request.getCustomerNumber())
                .bankCode(request.getBankCode())
                .loanAmount(loanAmount)
                .tenure(tenure)
                .loanStartDate(emiStartDate)
                .loanEndDate(emiStartDate.plusMonths(tenure))
                .loanAdminFee(adminFee)
                .annualInterestRate(BigDecimal.valueOf(12))
                .lateFee(BigDecimal.valueOf(100))
                .overdueRate(BigDecimal.valueOf(16))
                .penaltyRate(BigDecimal.valueOf(2))
                .status("BOOKED")
                .build();

        loanDetailRepository.save(loanDetail);

        List<EMISchedule> emiEntities = schedules.stream().map(s -> {
            EMISchedule e = new EMISchedule();
            e.setLoanNumber(loanNumber);
            e.setInstallmentNumber(s.getInstallmentNo());
            e.setDemandDate(s.getDemandDate());
            e.setEmiAmount(s.getEmiAmount());
            e.setPrincipal(s.getPrincipalComponent());
            e.setInterest(s.getInterestComponent());
            e.setInstallmentStartDate(s.getInstallmentStartDate());
            e.setInterestApplicableDays(s.getInterestApplicableDays());
            e.setStatus(s.getStatus());
            e.setBeginningBalance(loanAmount);
            e.setEndingBalance(s.getOutstandingPrincipal());
            return e;
        }).toList();
        emiScheduleRepository.saveAll(emiEntities);

        IsoTransferRequest isoTransferRequest = new IsoTransferRequest();
        isoTransferRequest.setBankCode(request.getBankCode());
        isoTransferRequest.setAccountNumber(request.getCustomerNumber());
        isoTransferRequest.setLoanNumber(loanNumber);
        isoTransferRequest.setAmount(loanAmount);

        log.info("Initiating transfer for loanNumber: {}", loanNumber);
            IsoTransferResponse isoTransferResponse = bankIsoClient.transfer(isoTransferRequest);
        if (isoTransferResponse.getStatus() == null || isoTransferResponse.getStatus().equals("FAILED")) {
            otpStore.remove(key);
            bookingStore.remove(key);
            throw new RuntimeException("Something went wrong while processing request. Please try again !");
        }
        log.info("Transfer completed with response: {}", isoTransferResponse);

        otpStore.remove(key);
        bookingStore.remove(key);

        LoanConfirmResponse response = new LoanConfirmResponse();
        response.setLoanNumber(loanNumber);
        BigDecimal transferAmount = isoTransferResponse.getAmount() != null
                ? isoTransferResponse.getAmount()
                : loanAmount;
        response.setLoanAmount(transferAmount);
        response.setTenure(tenure);
        response.setPaymentDate(paymentDate);
        response.setLoanAdminFee(adminFee);
        response.setLoanSchedules(schedules);
        response.setMessage(isoTransferResponse.getMessage());
        response.setStatus(isoTransferResponse.getStatus());
        response.setAccountNumber(isoTransferResponse.getAccountNumber());

        return response;
    }

    private String generateLoanNumber() {
        String timestampPart = String.format("%09d", System.currentTimeMillis() % 1_000_000_000L);
        String randomPart = String.format("%011d", Math.abs(new Random().nextLong()) % 1_000_000_00000L);
        return (timestampPart + randomPart).substring(0, 20);
    }

//    private void settingBeginningBalance(){
//        List<EMISchedule> emiSched = new ArrayList<EMISchedule>();
//        BigDecimal loanAmount = (BigDecimal) bookingStore.get("loanAmount");
//        int tenure = bookingStore.get("tenure").size();
//        emiSched.setBeginningBalance(loanAmount);
//        int i = 0;
//        do{
//            BigDecimal value = emiSched.setBeginningBalance(emiSched.getEndingBalance());
//            tenure ++;
//            }while (i <= tenure-1);
//        }
//    }
}

