package com.ebooks.bankisoservice.services;


import com.ebooks.bankisoservice.dtos.IsoTransferRequest;
import com.ebooks.bankisoservice.dtos.IsoTransferResponse;
import com.ebooks.bankisoservice.entities.BankTransaction;
import com.ebooks.bankisoservice.entities.Customer;
import com.ebooks.bankisoservice.enums.TransactionType;
import com.ebooks.bankisoservice.repositories.BankTransactionRepository;
import com.ebooks.bankisoservice.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankIsoService {
    private final CustomerRepository customerRepository;
    private final BankTransactionRepository bankTransactionRepository;

    @Transactional
    public IsoTransferResponse transfer(IsoTransferRequest request) {
        // VALIDATE INPUTS
        if (request.getBankCode() == null || request.getBankCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Bank code cannot be null or empty");
        }
        if (request.getAccountNumber() == null || request.getAccountNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be null or empty");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        String bankCode = request.getBankCode().trim();
        String customerAccount = request.getAccountNumber().trim();
        BigDecimal amount = request.getAmount();

        // FIXED: CORRECT PARAMETER ORDER - bankCode FIRST, then customerNumber
        Customer bankAccount = customerRepository
                .findCustomerByCustomerNumberAndBankCode(null, bankCode)  // Bank account (null customer)
                .orElseThrow(() -> new RuntimeException("Bank account not found for code " + bankCode));

        Customer customer = customerRepository
                .findCustomerByCustomerNumberAndBankCode(customerAccount, bankCode)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerAccount));

        // FIXED: Handle NULL BigDecimal
        BigDecimal bankBalance = bankAccount.getAmount() != null ? bankAccount.getAmount() : BigDecimal.ZERO;

        if (bankBalance.compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds in bank account");
        }

        // FIXED: Ensure non-null amounts
        bankAccount.setAmount(bankBalance.subtract(amount));
        BigDecimal customerBalance = customer.getAmount() != null ? customer.getAmount() : BigDecimal.ZERO;
        customer.setAmount(customerBalance.add(amount));

        customerRepository.save(bankAccount);
        customerRepository.save(customer);

        // Transaction logic remains same...
        String transactionId = UUID.randomUUID().toString();

        BankTransaction debit = new BankTransaction();
        debit.setAccountNumber(bankAccount.getCustomerNumber());
        debit.setTransactionId(transactionId);
        debit.setType(TransactionType.DEBIT);
        debit.setAmount(amount);
        debit.setParticularRemarks("Loan disbursement to " + customerAccount);
        debit.setBankCode(bankCode);
        debit.setValueDate(LocalDate.now());
        debit.setLoanNumber(request.getLoanNumber());
        bankTransactionRepository.save(debit);

        BankTransaction credit = new BankTransaction();
        credit.setTransactionId(transactionId);
        credit.setAccountNumber(customer.getCustomerNumber());
        credit.setType(TransactionType.CREDIT);
        credit.setAmount(amount);
        credit.setParticularRemarks("Loan received from bank");
        credit.setBankCode(bankCode);
        credit.setValueDate(LocalDate.now());
        credit.setLoanNumber(request.getLoanNumber());
        bankTransactionRepository.save(credit);

        IsoTransferResponse response = new IsoTransferResponse();
        response.setTransactionId(transactionId);  // FIXED: Use same transactionId
        response.setStatus("SUCCESS");
        response.setMessage("Transfer completed successfully");
        response.setAmount(amount);
        response.setAccountNumber(customerAccount);
        response.setBankCode(bankCode);
        return response;
    }
}