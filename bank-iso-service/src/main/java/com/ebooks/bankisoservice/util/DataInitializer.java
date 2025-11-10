package com.ebooks.bankisoservice.util;

import com.ebooks.bankisoservice.entities.Customer;
import com.ebooks.bankisoservice.repositories.CustomerRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final CustomerRepository customerRepository;

    @PostConstruct
    @Transactional
    public void init() {
        try {
            // Only initialize if no accounts exist
            if (customerRepository.count() == 0) {
                log.info("🚀 Initializing sample account data...");

                // Bank account (customer_number = NULL)
                Customer bankAccount = new Customer();
                bankAccount.setBankCode("BANK001");
                bankAccount.setAmount(new BigDecimal("10000000.00"));
                bankAccount.setCustomerNumber(null); // Bank account has no customer number
                customerRepository.save(bankAccount);
                log.info("   - Bank Account (BANK001, NULL): 10,000,000.00");

                // Customer accounts
                Customer customer1 = new Customer();
                customer1.setBankCode("BANK001");
                customer1.setAmount(new BigDecimal("5000.00")); // Matches log: 5,000.00
                customer1.setCustomerNumber("999999999");
                customerRepository.save(customer1);
                log.info("   - CUST_999999999: 5,000.00");

                Customer customer2 = new Customer();
                customer2.setBankCode("BANK002");
                customer2.setAmount(new BigDecimal("3000.00")); // Matches log: 3,000.00
                customer2.setCustomerNumber("888888888");
                customerRepository.save(customer2);
                log.info("   - CUST_888888888: 3,000.00");

                log.info("Sample account data initialized successfully");
            } else {
                log.info(" Accounts already exist in database");
            }
        } catch (Exception e) {
            log.error("❌ Failed to initialize sample data: {}", e.getMessage(), e);
        }
    }
}