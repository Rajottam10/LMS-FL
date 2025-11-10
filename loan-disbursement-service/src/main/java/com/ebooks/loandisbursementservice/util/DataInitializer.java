package com.ebooks.loandisbursementservice.util;

import com.ebooks.loandisbursementservice.entities.BankLoanConfig;
import com.ebooks.loandisbursementservice.entities.FoneloanLimits;
import com.ebooks.loandisbursementservice.enums.AdminFeeType;
import com.ebooks.loandisbursementservice.repositories.BankLoanConfigRepository;
import com.ebooks.loandisbursementservice.repositories.FoneloanLimitsRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final BankLoanConfigRepository bankLoanConfigRepository;
    private final FoneloanLimitsRepository foneloanLimitsRepository;

    @PostConstruct
    @Transactional
    public void init() {
        try {
            initializeBankConfigs();
            initializeFoneloanLimits();
            log.info("COMPLETE INITIALIZATION SUCCESSFUL!");
        } catch (Exception e) {
            log.error("Failed to initialize sample data: {}", e.getMessage(), e);
        }
    }

    private void initializeBankConfigs() {
        if (bankLoanConfigRepository.count() == 0) {
            log.info("Initializing bank loan configurations...");

            // BANK001: 5k-100k limits
            BankLoanConfig bank1 = new BankLoanConfig();
            bank1.setBankCode("BANK001");
            bank1.setMinAmount(new BigDecimal("5000.00"));
            bank1.setMaxAmount(new BigDecimal("100000.00"));
            bank1.setAnnualInterestRate(new BigDecimal("12.00"));
            bank1.setAdminFeeType(AdminFeeType.PERCENTAGE);
            bank1.setAdminFeeValue(new BigDecimal("1.00"));
            bankLoanConfigRepository.save(bank1);

            // BANK002: 10k-200k limits
            BankLoanConfig bank2 = new BankLoanConfig();
            bank2.setBankCode("BANK002");
            bank2.setMinAmount(new BigDecimal("10000.00"));
            bank2.setMaxAmount(new BigDecimal("200000.00"));
            bank2.setAnnualInterestRate(new BigDecimal("10.50"));
            bank2.setAdminFeeType(AdminFeeType.PERCENTAGE);
            bank2.setAdminFeeValue(new BigDecimal("0.75"));
            bankLoanConfigRepository.save(bank2);

            // BANK003: 2k-50k limits
            BankLoanConfig bank3 = new BankLoanConfig();
            bank3.setBankCode("BANK003");
            bank3.setMinAmount(new BigDecimal("2000.00"));
            bank3.setMaxAmount(new BigDecimal("50000.00"));
            bank3.setAnnualInterestRate(new BigDecimal("14.00"));
            bank3.setAdminFeeType(AdminFeeType.FLAT_RATE);
            bank3.setAdminFeeValue(new BigDecimal("500.00"));
            bankLoanConfigRepository.save(bank3);

            log.info("✅ 3 Bank configs: BANK001(5k-100k), BANK002(10k-200k), BANK003(2k-50k)");
        }
    }

    private void initializeFoneloanLimits() {
        if (foneloanLimitsRepository.count() == 0) {
            log.info("Initializing foneloan limits...");

            // CUSTOMER 999999999 - BANK001
            createFoneloanLimit("999999999", "John Smith", "BANK001", 1,  null, BigDecimal.valueOf(10000.00)); // 1-month: ₹10k
            createFoneloanLimit("999999999", "John Smith", "BANK001", 3,  BigDecimal.valueOf(25000.00), BigDecimal.valueOf(10000.00));
            createFoneloanLimit("999999999", "John Smith", "BANK001", 6,  BigDecimal.valueOf(45000.00), BigDecimal.valueOf(10000.00));
            createFoneloanLimit("999999999", "John Smith", "BANK001", 9,  BigDecimal.valueOf(65000.00), BigDecimal.valueOf(10000.00));
            createFoneloanLimit("999999999", "John Smith", "BANK001", 12, BigDecimal.valueOf(85000.00), BigDecimal.valueOf(10000.00));

            // CUSTOMER 888888888 - BANK001
            createFoneloanLimit("888888888", "Sarah Johnson", "BANK001", 1, null, BigDecimal.valueOf(12000.00));
            createFoneloanLimit("888888888", "Sarah Johnson", "BANK001", 3,  BigDecimal.valueOf(35000.00), BigDecimal.valueOf(12000.00));
            // ... repeat for 6,9,12

            // CUSTOMER 777777777 - BANK002
            createFoneloanLimit("777777777", "Michael Brown", "BANK002", 1, null, BigDecimal.valueOf(15000.00));
            // ... repeat

            log.info("3 Customers | 15 Limits | 1-month limits added & separated");
        }
    }

    private void createFoneloanLimit(String customerNumber, String customerName, String bankCode,
                                     Integer emiMonths, BigDecimal emiMaxAmount, BigDecimal oneMonthLimit) {
        FoneloanLimits limit = new FoneloanLimits();
        limit.setCustomerNumber(customerNumber);
        limit.setCustomerName(customerName);
        limit.setBankCode(bankCode);
        limit.setEmiMonths(emiMonths);
        limit.setEmiMaxAmount(emiMaxAmount);
        limit.setOneMonthRecommendedLimit(oneMonthLimit); // ← NOW DIFFERENT
        foneloanLimitsRepository.save(limit);
        log.debug("Saved: {} | {}m | emiMax: ₹{} | 1mLimit: ₹{}", customerNumber, emiMonths, emiMaxAmount, oneMonthLimit);
    }
}