package com.ebooks.prepaymentservice.services;

import com.ebooks.commonmoduleloan.entities.LoanDetail;
import com.ebooks.commonmoduleloan.repositories.EMIScheduleRepository;
import com.ebooks.commonmoduleloan.repositories.InterestInfoRepository;
import com.ebooks.commonmoduleloan.repositories.LateFeeInfoRepository;
import com.ebooks.commonmoduleloan.repositories.OverdueInfoRepository;
import com.ebooks.commonmoduleloan.repositories.PenaltyInfoRepository;
import com.ebooks.prepaymentservice.dtos.*;
import com.ebooks.commonmoduleloan.repositories.LoanDetailRepository;
import com.ebooks.prepaymentservice.entities.PaymentRecord;
import com.ebooks.prepaymentservice.entities.PrepaymentLog;
import com.ebooks.prepaymentservice.repositories.PaymentRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PrepaymentService {

    private final LoanDetailRepository loanDetailRepo;
    private final InterestInfoRepository interestRepo;
    private final OverdueInfoRepository overdueRepo;
    private final PenaltyInfoRepository penaltyRepo;
    private final LateFeeInfoRepository lateFeeRepo;
    private final EMIScheduleRepository emiScheduleRepo;
    private final PaymentRecordRepository paymentRecordRepo;
    private final BankIsoClient bankIsoClient;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private final LoanDetailRepository loanDetailRepository;

    // 1. INQUIRY
    public PrepaymentInquiry inquiry(String loanNumber) {
        LoanDetail loan = loanDetailRepo.findLoanDetailByLoanNumber(loanNumber)
                .orElseThrow(() -> new IllegalArgumentException("Invalid loan number: " + loanNumber));

        if (loan.getStatus().equals("SETTLED")) {
            throw new RuntimeException("Loan is already settled or fully paid off.");
        }

//        LocalDate inquiryDate = LocalDate.now();
//        boolean accrualDone = accrualService.isAccrualDoneTillDate(loanNumber, inquiryDate);
//        if (!accrualDone) {
//            throw new RuntimeException("Accrual not performed till " + inquiryDate + ". Prepayment not allowed.");
//        }

        // Dues
        BigDecimal totalInterest = nullSafe(interestRepo.getDailyAccrualsForLoan(loanNumber));
        BigDecimal totalOverdue = nullSafe(overdueRepo.getTotalOverdueInterestForLoan(loanNumber));
        BigDecimal totalPenalty = nullSafe(penaltyRepo.getTotalPenaltyInterestForLoan(loanNumber));
        BigDecimal totalLateFee = nullSafe(lateFeeRepo.getTotalLateFeeForLoan(loanNumber));

        // Paid
        BigDecimal paidPrincipal = nullSafe(emiScheduleRepo.totalPaidPrincipalByLoanNumber(loanNumber));
        BigDecimal paidInterest = nullSafe(emiScheduleRepo.totalPaidInterestByLoanNumber(loanNumber));
        BigDecimal paidOverdue = nullSafe(emiScheduleRepo.totalPaidOverdueInterestByLoanNumber(loanNumber));
        BigDecimal paidPenalty = nullSafe(emiScheduleRepo.totalPaidPenaltyInterestByLoanNumber(loanNumber));
        BigDecimal paidLateFee = nullSafe(emiScheduleRepo.totalPaidLateFeeByLoanNumber(loanNumber));

        // Net dues
        BigDecimal netInterest = totalInterest.subtract(paidInterest);
        BigDecimal netOverdue = totalOverdue.subtract(paidOverdue);
        BigDecimal netPenalty = totalPenalty.subtract(paidPenalty);
        BigDecimal netLateFee = totalLateFee.subtract(paidLateFee);

        BigDecimal interestSum = netInterest
                .add(netOverdue)
                .add(netPenalty)
                .add(netLateFee);

        // Prepay charge
        BigDecimal prePayCharge = BigDecimal.valueOf(250);

        // Payable
        BigDecimal payablePrincipal = loan.getLoanAmount().subtract(paidPrincipal);
        BigDecimal totalPayable = interestSum.add(payablePrincipal).add(prePayCharge);

        // Build response
        PrepaymentInquiry resp = new PrepaymentInquiry();
        PayableDetails payable = new PayableDetails();
        payable.setPrincipal(payablePrincipal);
        payable.setTotal(totalPayable);
        payable.setInterest(netInterest);
        payable.setLateFee(netLateFee);
        payable.setOverdueInterest(netOverdue);
        payable.setPenaltyInterest(netPenalty);
        payable.setPrePayCharge(prePayCharge);
        resp.setPayableDetails(payable);

        LoanDetails loanDet = new LoanDetails();
        loanDet.setProcessOnDate(LocalDate.now().format(DATE_FMT));
        loanDet.setLoanAdminFee(loan.getLoanAdminFee());
        loanDet.setPaidAmount(loan.getPaidAmount());
        loanDet.setLoanAmount(loan.getLoanAmount());
        loanDet.setRemainingAmount(totalPayable);
        resp.setLoanDetails(loanDet);

        return resp;
    }

    // 2. CONFIRM
    @Transactional
    public String confirmPrepayment(String loanNumber) {

        // 1. Load loan
        LoanDetail loan = loanDetailRepo.findLoanDetailByLoanNumber(loanNumber)
                .orElseThrow(() -> new RuntimeException("Invalid loan number: " + loanNumber));

        BigDecimal totalPaidPrincipal = nullSafe(emiScheduleRepo.totalPaidPrincipalByLoanNumber(loanNumber));
        BigDecimal remainingAmount = loan.getLoanAmount().subtract(totalPaidPrincipal);

        if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Loan is already settled or fully paid off.");
        }

        // ────── 2. Dues (accrued) ──────
        BigDecimal totalInterest = nullSafe(interestRepo.getDailyAccrualsForLoan(loanNumber));
        BigDecimal totalOverdueInterest = nullSafe(overdueRepo.getTotalOverdueInterestForLoan(loanNumber));
        BigDecimal totalPenaltyInterest = nullSafe(penaltyRepo.getTotalPenaltyInterestForLoan(loanNumber));
        BigDecimal totalLateFee = nullSafe(lateFeeRepo.getTotalLateFeeForLoan(loanNumber));

        // ────── 3. Pre-pay charge ──────
        BigDecimal prepayCharge = BigDecimal.valueOf(250);

        // ────── 4. Already paid amounts ──────
        BigDecimal totalPaidInterest = nullSafe(emiScheduleRepo.totalPaidInterestByLoanNumber(loanNumber));
        BigDecimal totalPaidOverdueInterest = nullSafe(emiScheduleRepo.totalPaidOverdueInterestByLoanNumber(loanNumber));
        BigDecimal totalPaidPenaltyInterest = nullSafe(emiScheduleRepo.totalPaidPenaltyInterestByLoanNumber(loanNumber));
        BigDecimal totalPaidLateFee = nullSafe(emiScheduleRepo.totalPaidLateFeeByLoanNumber(loanNumber));

        // ────── 5. Net payable ──────
        BigDecimal payablePrincipal = loan.getLoanAmount().subtract(totalPaidPrincipal);
        BigDecimal payableInterest = totalInterest.subtract(totalPaidInterest);
        BigDecimal payableOverdue = totalOverdueInterest.subtract(totalPaidOverdueInterest);
        BigDecimal payablePenalty = totalPenaltyInterest.subtract(totalPaidPenaltyInterest);
        BigDecimal payableLateFee = totalLateFee.subtract(totalPaidLateFee);

        BigDecimal finalAmount = payablePrincipal
                .add(payableInterest)
                .add(payableOverdue)
                .add(payablePenalty)
                .add(payableLateFee)
                .add(prepayCharge);

        // ────── 6. ISO transfer (the missing piece) ──────
        String BANK_SETTLEMENT_ACCOUNT = "111111111";

        IsoTransferRequest transferReq = IsoTransferRequest.builder()
                .bankCode(loan.getBankCode())
                .accountNumber(loan.getCustomerNumber())
                .creditAccount(BANK_SETTLEMENT_ACCOUNT)
                .loanNumber(loanNumber)
                .amount(finalAmount)
                .build();

        IsoTransferResponse isoResp;
        try {
            log.info("Calling Bank-ISO transfer for loan {} – amount {}", loanNumber, finalAmount);
            isoResp = bankIsoClient.transfer(transferReq);
        } catch (Exception ex) {
            log.error("ISO transfer failed for loan {}", loanNumber, ex);
            return "ERROR IN PROCESSING PREPAYMENT.";
        }

        if (!"SUCCESS".equalsIgnoreCase(isoResp.getStatus())) {
            log.warn("ISO rejected prepayment for loan {} – {}", loanNumber, isoResp.getMessage());
            return "ERROR IN PROCESSING PREPAYMENT.";
        }

        // ────── 7. Record payment (exactly as in screenshot) ──────
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setLoanNumber(loanNumber);
        paymentRecord.setType("PREPAYMENT");
        paymentRecord.setPaymentDate(LocalDate.now());
        paymentRecord.setAmount(finalAmount);
        paymentRecord.setPaidPrincipal(payablePrincipal);
        paymentRecord.setPaidInterest(payableInterest);
        paymentRecord.setPaidOverdueInterest(payableOverdue);
        paymentRecord.setPaidPenaltyInterest(payablePenalty);
        paymentRecord.setPaidLateFee(payableLateFee);
        paymentRecord.setStatus("SUCCESS");
        paymentRecordRepo.save(paymentRecord);

        // ────── 8. Log prepayment ──────
        PrepaymentLog prepaymentLog = new PrepaymentLog();
        prepaymentLog.setLoanNumber(loanNumber);
        prepaymentLog.setBankCode(loan.getBankCode());
        prepaymentLog.setPrepayDate(LocalDate.now());
        prepaymentLog.setTotalPaid(finalAmount);
        prepaymentLog.setPaidPrincipal(payablePrincipal);
        prepaymentLog.setPaidInterest(payableInterest);
        prepaymentLog.setPaidOverdueInterest(payableOverdue);
        prepaymentLog.setPaidPenaltyInterest(payablePenalty);
        prepaymentLog.setPaidLateFee(payableLateFee);
        prepaymentLog.setAccruedInterest(totalInterest);
        prepaymentLog.setAccruedOverdueInterest(totalOverdueInterest);
        prepaymentLog.setAccruedPenaltyInterest(totalPenaltyInterest);
        prepaymentLog.setAccruedLateFee(totalLateFee);
        prepaymentLog.setPrepayCharge(prepayCharge);
        prepaymentLog.setStatus("SUCCESS");
        prepaymentLog.setPrincipal(remainingAmount);

        // ────── 9. CLOSE THE LOAN (SETTLED) & MARK ALL EMIs PAID ──────
        loan.setStatus("SETTLED");
        loan.setPaidAmount(nullSafe(loan.getPaidAmount()).add(finalAmount));
        loanDetailRepo.save(loan);

        emiScheduleRepo.findByLoanNumber(loanNumber).forEach(emi -> {
            emi.setStatus("PAID");
            emi.setPaidPrincipal(emi.getPrincipal());
            emi.setPaidInterest(emi.getInterest().add(payableInterest));
            emi.setPaidOverdueInterest(emi.getPaidOverdueInterest().add(payableOverdue));
            emi.setPaidPenaltyInterest(emi.getPaidPenaltyInterest().add(payablePenalty));
            emi.setPaidLateFee(emi.getPaidLateFee().add(payableLateFee));
        });
        emiScheduleRepo.saveAll(emiScheduleRepo.findByLoanNumber(loanNumber));

        return "SUFFICIENT BALANCE IN CUSTOMER ACCOUNT. PREPAYMENT SUCCESSFUL!!!";
    }

    // Helpers
    private BigDecimal nullSafe(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val;
    }

}