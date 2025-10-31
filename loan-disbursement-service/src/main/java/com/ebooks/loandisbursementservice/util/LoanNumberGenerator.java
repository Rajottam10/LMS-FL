package com.ebooks.loandisbursementservice.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
@Component
public class LoanNumberGenerator {

    public String generate20DigitLoanNumber(String bankCode) {
        // Ensure bank code is exactly 4 chars (take first 4)
        String shortBankCode = bankCode.length() > 4 ? bankCode.substring(0, 4) : bankCode;

        // Current date as YYMMDD (6 chars)
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));

        // Nanosecond precision for uniqueness (10 chars)
        String nano = String.format("%010d", System.nanoTime() % 10000000000L);

        String loanNumber = shortBankCode + date + nano;

        System.out.println("Final loan number: " + loanNumber + " (length: " + loanNumber.length() + ")");

        return loanNumber;
    }
}