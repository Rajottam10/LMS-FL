package com.ebooks.prepaymentservice.jobs;

import com.ebooks.commonmoduleloan.entities.EMISchedule;
import com.ebooks.commonmoduleloan.entities.LoanDetail;
import com.ebooks.commonmoduleloan.repositories.EMIScheduleRepository;
import com.ebooks.commonmoduleloan.repositories.InterestInfoRepository;
import com.ebooks.commonmoduleloan.repositories.LateFeeInfoRepository;
import com.ebooks.commonmoduleloan.repositories.LoanDetailRepository;
import com.ebooks.commonmoduleloan.repositories.OverdueInfoRepository;
import com.ebooks.commonmoduleloan.repositories.PenaltyInfoRepository;
import com.ebooks.commonmoduleloan.services.EMIScheduleServiceImpl;
import com.ebooks.prepaymentservice.dtos.IsoTransferRequest;
import com.ebooks.prepaymentservice.dtos.IsoTransferResponse;
import com.ebooks.prepaymentservice.entities.PaymentRecord;
import com.ebooks.prepaymentservice.entities.PrepaymentConfig;
import com.ebooks.prepaymentservice.entities.PrepaymentLog;
import com.ebooks.prepaymentservice.repositories.PaymentRecordRepository;
import com.ebooks.prepaymentservice.repositories.PrepaymentConfigRepository;
import com.ebooks.prepaymentservice.repositories.PrepaymentLogRepository;
import com.ebooks.prepaymentservice.services.BankIsoClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementScheduler {

    private final BankIsoClient bankIsoClient;
    private final EMIScheduleRepository emiScheduleRepo;
    private final LoanDetailRepository loanDetailRepo;
    private final InterestInfoRepository interestRepo;
    private final OverdueInfoRepository overdueRepo;
    private final PenaltyInfoRepository penaltyRepo;
    private final LateFeeInfoRepository lateFeeRepo;
    private final PaymentRecordRepository paymentRecordRepo;
    private final PrepaymentConfigRepository prepaymentConfigRepo;
    private final PrepaymentLogRepository prepaymentLogRepo;
    private final EMIScheduleServiceImpl emiScheduleService;

//    @Scheduled(cron = "50 * * * * *")
    @Transactional
    public void loanSettlementJob() {
        LocalDate today = LocalDate.now();
        log.info("Starting loan settlement job for demand date up to: {}", today);

        List<EMISchedule> demandDateUnpaidEmi =
                emiScheduleRepo.findAllByEmiDateLessThanEqualAndStatusNot(today, "PAID");

        if (demandDateUnpaidEmi.isEmpty()) {
            log.info("No unpaid EMI found with demand date up to today. Job finished.");
            return;
        }

        Map<String, List<EMISchedule>> emiGroupedByLoan = demandDateUnpaidEmi.stream()
                .collect(Collectors.groupingBy(EMISchedule::getLoanNumber));

        log.info("Found unpaid EMIs for {} loans.", emiGroupedByLoan.size());

        for (Map.Entry<String, List<EMISchedule>> entry : emiGroupedByLoan.entrySet()) {
            String loanNumber = entry.getKey();
            List<EMISchedule> loanEmis = entry.getValue().stream()
                    .sorted(Comparator.comparing(EMISchedule::getInstallmentNumber))
                    .toList();

            Optional<LoanDetail> optLoanDetail = loanDetailRepo.findLoanDetailByLoanNumber(loanNumber);
            LoanDetail loanDetail = optLoanDetail.orElse(null);

            if (loanDetail == null) {
                log.warn("No LoanDetail found for loan number: {}. Skipping.", loanNumber);
                continue;
            }

            if ("SETTLED".equals(loanDetail.getStatus()) || "CLOSED".equals(loanDetail.getStatus())) {
                log.info("Loan {} already {}. Skipping.", loanNumber, loanDetail.getStatus());
                continue;
            }

            log.info("Processing settlement for loan: {} ({} unpaid EMIs).", loanNumber, loanEmis.size());

            // === 1. Calculate Remaining Amounts ===
            Map<Long, BigDecimal> remainingPrincipalPerEmi = new HashMap<>();
            Map<Long, BigDecimal> remainingInterestPerEmi = new HashMap<>();
            Map<Long, BigDecimal> remainingOverduePerEmi = new HashMap<>();
            Map<Long, BigDecimal> remainingPenaltyPerEmi = new HashMap<>();
            Map<Long, BigDecimal> remainingLateFeePerEmi = new HashMap<>();

            BigDecimal totalRemainingPrincipal = BigDecimal.ZERO;
            BigDecimal totalRemainingInterest = BigDecimal.ZERO;
            BigDecimal totalRemainingOverdue = BigDecimal.ZERO;
            BigDecimal totalRemainingPenalty = BigDecimal.ZERO;
            BigDecimal totalRemainingLateFee = BigDecimal.ZERO;

            for (EMISchedule emi : loanEmis) {
                Long emiId = emi.getId();

                BigDecimal remPrincipal = safe(emi.getPrincipal()).subtract(safe(emi.getPaidPrincipal()));
                remainingPrincipalPerEmi.put(emiId, nonNegative(remPrincipal));
                totalRemainingPrincipal = totalRemainingPrincipal.add(nonNegative(remPrincipal));

                BigDecimal totalInterest = interestRepo.getTotalInterestForEmi(loanNumber, emi.getInstallmentNumber());
                BigDecimal remInterest = safe(totalInterest).subtract(safe(emi.getPaidInterest()));
                remainingInterestPerEmi.put(emiId, nonNegative(remInterest));
                totalRemainingInterest = totalRemainingInterest.add(nonNegative(remInterest));

                BigDecimal totalOverdue, totalPenalty, totalLateFee;
                if (emiScheduleService.isLastInstallment(emi)) {
                    totalOverdue = safe(overdueRepo.getTotalOverdueInterestForEmi(loanNumber, emi.getInstallmentNumber())).multiply(BigDecimal.valueOf(2));
                    totalPenalty = safe(penaltyRepo.getTotalPenaltyInterestForEmi(loanNumber, emi.getInstallmentNumber())).multiply(BigDecimal.valueOf(2));
                    totalLateFee = safe(lateFeeRepo.getTotalLateFeeForEmi(loanNumber, emi.getInstallmentNumber())).multiply(BigDecimal.valueOf(2));
                } else {
                    totalOverdue = overdueRepo.getTotalOverdueInterestForEmi(loanNumber, emi.getInstallmentNumber());
                    totalPenalty = penaltyRepo.getTotalPenaltyInterestForEmi(loanNumber, emi.getInstallmentNumber());
                    totalLateFee = lateFeeRepo.getTotalLateFeeForEmi(loanNumber, emi.getInstallmentNumber());
                }

                BigDecimal remOverdue = safe(totalOverdue).subtract(safe(emi.getPaidOverdueInterest()));
                remainingOverduePerEmi.put(emiId, nonNegative(remOverdue));
                totalRemainingOverdue = totalRemainingOverdue.add(nonNegative(remOverdue));

                BigDecimal remPenalty = safe(totalPenalty).subtract(safe(emi.getPaidPenaltyInterest()));
                remainingPenaltyPerEmi.put(emiId, nonNegative(remPenalty));
                totalRemainingPenalty = totalRemainingPenalty.add(nonNegative(remPenalty));

                BigDecimal remLateFee = safe(totalLateFee).subtract(safe(emi.getPaidLateFee()));
                remainingLateFeePerEmi.put(emiId, nonNegative(remLateFee));
                totalRemainingLateFee = totalRemainingLateFee.add(nonNegative(remLateFee));
            }

            // === 2. Get Customer Balance ===
            BigDecimal customerBalance;
            try {
                customerBalance = getCustomerBalance(loanDetail.getCustomerNumber());
                log.info("Customer {} balance: {}", loanDetail.getCustomerNumber(), customerBalance);
            } catch (Exception ex) {
                log.error("Failed to get balance for customer {} (loan {}). Skipping.", loanDetail.getCustomerNumber(), loanNumber, ex);
                continue;
            }

            if (customerBalance.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("Customer {} has zero balance. Skipping loan {}.", loanDetail.getCustomerNumber(), loanNumber);
                continue;
            }

            // === 3. Calculate Prepay Charge from Config ===
            BigDecimal prepayCharge = BigDecimal.ZERO;
            PrepaymentConfig config = prepaymentConfigRepo.findByBankCode(loanDetail.getBankCode()).orElse(null);
            if (config != null) {
                BigDecimal base = totalRemainingPrincipal;
                switch (config.getChargeType()) {
                    case "PERCENTAGE" -> prepayCharge = base.multiply(config.getChargeValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    case "FLAT" -> prepayCharge = config.getChargeValue();
                    case "GREATER" -> {
                        BigDecimal percent = base.multiply(config.getChargeValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                        prepayCharge = percent.max(config.getChargeValue());
                    }
                }
            }

            // === 4. KnockOff in Priority Order ===
            AtomicReference<BigDecimal> remainingBalance = new AtomicReference<>(customerBalance);
            BigDecimal paidLateFee = deductComponent(loanEmis, remainingLateFeePerEmi, "PaidLateFee", remainingBalance);
            BigDecimal paidPenaltyInterest = deductComponent(loanEmis, remainingPenaltyPerEmi, "PaidPenaltyInterest", remainingBalance);
            BigDecimal paidOverdueInterest = deductComponent(loanEmis, remainingOverduePerEmi, "PaidOverdueInterest", remainingBalance);
            BigDecimal paidInterest = deductComponent(loanEmis, remainingInterestPerEmi, "PaidInterest", remainingBalance);
            BigDecimal paidPrincipalAmount = deductComponent(loanEmis, remainingPrincipalPerEmi, "PaidPrincipal", remainingBalance);

            BigDecimal totalDeducted = paidLateFee
                    .add(paidPenaltyInterest)
                    .add(paidOverdueInterest)
                    .add(paidInterest)
                    .add(paidPrincipalAmount)
                    .add(prepayCharge);

            if (totalDeducted.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("Loan {}: nothing deducted this run.", loanNumber);
                continue;
            }

            // === 5. ISO Transfer + DB Updates (Atomic) ===
            PrepaymentLog prepaymentLog = PrepaymentLog.builder()
                    .loanNumber(loanNumber)
                    .bankCode(loanDetail.getBankCode())
                    .prepayDate(LocalDate.now())
                    .principal(totalRemainingPrincipal)
                    .accruedInterest(totalRemainingInterest)
                    .accruedOverdueInterest(totalRemainingOverdue)
                    .accruedPenaltyInterest(totalRemainingPenalty)
                    .accruedLateFee(totalRemainingLateFee)
                    .prepayCharge(prepayCharge)
                    .totalPaid(totalDeducted)
                    .paidPrincipal(paidPrincipalAmount)
                    .paidInterest(paidInterest)
                    .paidOverdueInterest(paidOverdueInterest)
                    .paidPenaltyInterest(paidPenaltyInterest)
                    .paidLateFee(paidLateFee)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            try {
                // === ISO Transfer ===
                IsoTransferRequest transferReq = IsoTransferRequest.builder()
                        .bankCode(loanDetail.getBankCode())
                        .accountNumber(loanDetail.getCustomerNumber())
                        .creditAccount("BANK_SETTLEMENT")
                        .loanNumber(loanNumber)
                        .amount(totalDeducted)
                        .build();

                log.info("Triggering ISO prepay for loan {}: amount {}", loanNumber, totalDeducted);
                IsoTransferResponse isoResp = bankIsoClient.transfer(transferReq);

                if (!"SUCCESS".equalsIgnoreCase(isoResp.getStatus())) {
                    throw new RuntimeException("ISO failed: " + isoResp.getMessage());
                }

                prepaymentLog.setStatus("SUCCESS");
                prepaymentLog.setTransactionId(isoResp.getTransactionId());
                log.info("ISO prepay SUCCESS for loan {}: txId={}", loanNumber, isoResp.getTransactionId());

                // === Update Loan ===
                loanDetail.setPaidAmount(safe(loanDetail.getPaidAmount()).add(totalDeducted));
                loanDetailRepo.save(loanDetail);

                // === UPDATE EMI STATUS (USING PROXY) ===
                for (EMISchedule emi : loanEmis) {
                    Long emiId = emi.getId();

                    boolean allComponentsPaid =
                            isZero(remainingPrincipalPerEmi.getOrDefault(emiId, BigDecimal.ZERO)) &&
                                    isZero(remainingInterestPerEmi.getOrDefault(emiId, BigDecimal.ZERO)) &&
                                    isZero(remainingOverduePerEmi.getOrDefault(emiId, BigDecimal.ZERO)) &&
                                    isZero(remainingPenaltyPerEmi.getOrDefault(emiId, BigDecimal.ZERO)) &&
                                    isZero(remainingLateFeePerEmi.getOrDefault(emiId, BigDecimal.ZERO));

                    String currentStatus = emi.getStatus();
                    String targetStatus = allComponentsPaid ? "PAID" : "OVERDUE";

                    if (!targetStatus.equals(currentStatus)) {
                        EMISchedule proxy = emiScheduleRepo.getReferenceById(emiId);
                        proxy.setStatus(targetStatus);
                        log.info("EMI {} for loan {} status updated: {} → {}",
                                emi.getInstallmentNumber(), loanNumber, currentStatus, targetStatus);
                    }
                }

                // === Payment Record ===
                PaymentRecord record = new PaymentRecord();
                record.setLoanNumber(loanNumber);
                record.setType("SETTLEMENT");
                record.setPaymentDate(LocalDate.now());
                record.setAmount(totalDeducted);
                record.setPaidPrincipal(paidPrincipalAmount);
                record.setPaidInterest(paidInterest);
                record.setPaidOverdueInterest(paidOverdueInterest);
                record.setPaidPenaltyInterest(paidPenaltyInterest);
                record.setPaidLateFee(paidLateFee);
                record.setStatus("SUCCESS");
                paymentRecordRepo.save(record);

                // === Check Loan Settled ===
                List<EMISchedule> allEmis = emiScheduleRepo.findByLoanNumber(loanNumber);
                if (allEmis.stream().allMatch(e -> "PAID".equals(e.getStatus()))) {
                    loanDetail.setStatus("SETTLED");
                    loanDetailRepo.save(loanDetail);
                    log.info("Loan {} fully SETTLED.", loanNumber);
                }

            } catch (Exception ex) {
                log.error("Settlement failed for loan {}: {}", loanNumber, ex.getMessage());
                prepaymentLog.setStatus("FAILED");
                prepaymentLog.setFailureReason(ex.getMessage());
                throw ex;
            } finally {
                prepaymentLog.setUpdatedAt(LocalDateTime.now());
                prepaymentLogRepo.save(prepaymentLog);
                log.info("Prepayment log saved for loan {}: {}", loanNumber, prepaymentLog.getStatus());
            }

            log.info("Loan {}: total deducted this run: {}", loanNumber, totalDeducted);
        }

        log.info("Loan settlement job finished.");
    }

    // === Helper: Deduct Component
    private BigDecimal deductComponent(List<EMISchedule> emis, Map<Long, BigDecimal> remainingMap, String field, AtomicReference<BigDecimal> balanceRef) {
        BigDecimal paid = BigDecimal.ZERO;
        BigDecimal totalRemaining = remainingMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal balance = balanceRef.get();

        if (balance.compareTo(totalRemaining) >= 0) {
            for (EMISchedule emi : emis) {
                BigDecimal deduct = remainingMap.getOrDefault(emi.getId(), BigDecimal.ZERO);
                if (isZero(deduct)) continue;

                EMISchedule managed = emiScheduleRepo.getReferenceById(emi.getId());
                updatePaid(managed, field, deduct);
                paid = paid.add(deduct);
                balance = balance.subtract(deduct);
            }
        } else {
            for (EMISchedule emi : emis) {
                if (balance.compareTo(BigDecimal.ZERO) <= 0) break;
                BigDecimal deduct = remainingMap.getOrDefault(emi.getId(), BigDecimal.ZERO);
                if (isZero(deduct)) continue;
                BigDecimal apply = balance.min(deduct);

                EMISchedule managed = emiScheduleRepo.getReferenceById(emi.getId());
                updatePaid(managed, field, apply);
                paid = paid.add(apply);
                balance = balance.subtract(apply);
                if (apply.compareTo(deduct) < 0) break;
            }
        }
        balanceRef.set(balance);
        return paid;
    }

    private void updatePaid(EMISchedule emi, String field, BigDecimal amount) {
        switch (field) {
            case "PaidPrincipal" -> emi.setPaidPrincipal(safe(emi.getPaidPrincipal()).add(amount));
            case "PaidInterest" -> emi.setPaidInterest(safe(emi.getPaidInterest()).add(amount));
            case "PaidOverdueInterest" -> emi.setPaidOverdueInterest(safe(emi.getPaidOverdueInterest()).add(amount));
            case "PaidPenaltyInterest" -> emi.setPaidPenaltyInterest(safe(emi.getPaidPenaltyInterest()).add(amount));
            case "PaidLateFee" -> emi.setPaidLateFee(safe(emi.getPaidLateFee()).add(amount));
        }
    }

    // === Helpers ===
    private BigDecimal safe(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
    private BigDecimal nonNegative(BigDecimal v) { return v == null || v.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : v; }
    private boolean isZero(BigDecimal v) { return v == null || v.compareTo(BigDecimal.ZERO) == 0; }
    private BigDecimal getCustomerBalance(String customerNumber) { return bankIsoClient.getBalance(customerNumber); }
}