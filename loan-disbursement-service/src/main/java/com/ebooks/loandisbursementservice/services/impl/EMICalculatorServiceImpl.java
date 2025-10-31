//package com.ebooks.loandisbursementservice.services.impl;
//
//import com.ebooks.loandisbursementservice.entities.EmiSchedule;
//import com.ebooks.loandisbursementservice.services.EMICalculatorService;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class EMICalculatorServiceImpl implements EMICalculatorService {
//
//    private static final BigDecimal ANNUAL_INTEREST_RATE = new BigDecimal("12.0"); // 12%
//    private static final BigDecimal MONTHLY_INTEREST_RATE = ANNUAL_INTEREST_RATE
//            .divide(new BigDecimal("1200"), 6, RoundingMode.HALF_UP); // 12% / 12 / 100
//    private static final BigDecimal ADMIN_FEE_PERCENTAGE = new BigDecimal("0.01"); // 1%
//
//    @Override
//    public BigDecimal calculateEMI(BigDecimal loanAmount, Integer tenureMonths) {
//        // EMI = [P x R x (1+R)^N] / [(1+R)^N - 1]
//        BigDecimal rate = MONTHLY_INTEREST_RATE;
//        BigDecimal onePlusRate = BigDecimal.ONE.add(rate);
//        BigDecimal power = onePlusRate.pow(tenureMonths);
//
//        BigDecimal numerator = loanAmount.multiply(rate).multiply(power);
//        BigDecimal denominator = power.subtract(BigDecimal.ONE);
//
//        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
//    }
//
//    @Override
//    public BigDecimal calculateAdminFee(BigDecimal loanAmount) {
//        return loanAmount.multiply(ADMIN_FEE_PERCENTAGE).setScale(2, RoundingMode.HALF_UP);
//    }
//
//    @Override
//    public BigDecimal getInterestRate() {
//        return ANNUAL_INTEREST_RATE;
//    }
//
//    @Override
//    public List<EmiSchedule> generateEMISchedules(String loanNumber, BigDecimal loanAmount,
//                                                  Integer tenure, LocalDate paymentDate, String bankCode) {
//        List<EmiSchedule> schedules = new ArrayList<>();
//        BigDecimal emi = calculateEMI(loanAmount, tenure);
//        BigDecimal remainingPrincipal = loanAmount;
//
//        for (int i = 1; i <= tenure; i++) {
//            LocalDate dueDate = paymentDate.plusMonths(i - 1);
//
//            // Calculate interest for this month
//            BigDecimal monthlyInterest = remainingPrincipal.multiply(MONTHLY_INTEREST_RATE)
//                    .setScale(2, RoundingMode.HALF_UP);
//
//            // Calculate principal for this month
//            BigDecimal monthlyPrincipal = emi.subtract(monthlyInterest);
//
//            // Adjust for last installment
//            if (i == tenure) {
//                monthlyPrincipal = remainingPrincipal;
//            }
//
//            // Update remaining principal
//            remainingPrincipal = remainingPrincipal.subtract(monthlyPrincipal);
//            if (remainingPrincipal.compareTo(BigDecimal.ZERO) < 0) {
//                remainingPrincipal = BigDecimal.ZERO;
//            }
//
//            EmiSchedule schedule = new EmiSchedule();
//            schedule.setLoanNumber(loanNumber);
//            schedule.setInstallmentNumber(i);
//            schedule.setDueDate(dueDate);
//            schedule.setPrincipalAmount(monthlyPrincipal);
//            schedule.setInterestAmount(monthlyInterest);
//            schedule.setTotalEmi(emi);
//            schedule.setRemainingPrincipal(remainingPrincipal);
//            schedule.setPaymentStatus("PENDING");
//            schedule.setBankCode(bankCode);
//
//            schedules.add(schedule);
//        }
//
//        return schedules;
//    }
//}
