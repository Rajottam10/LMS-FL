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

    public BigDecimal getBalance(String customerNumber) {
        return customerRepository
                .findCustomerByCustomerNumberAndBankCode(customerNumber, "BANK001")
                .map(c -> c.getAmount() != null ? c.getAmount() : BigDecimal.ZERO)
                .orElseThrow(() -> new RuntimeException("Invalid customer: " + customerNumber));
    }

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

    @Transactional
    public IsoTransferResponse prepay(IsoTransferRequest request) {
        String bankCode = request.getBankCode();
        String customerAccount = request.getAccountNumber();  // ← DEBIT FROM
        BigDecimal amount = request.getAmount();

        Customer bank = customerRepository
                .findCustomerByCustomerNumberAndBankCode(null, bankCode)
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        Customer customer = customerRepository
                .findCustomerByCustomerNumberAndBankCode(customerAccount, bankCode)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerAccount));

        BigDecimal custBal = nullSafe(customer.getAmount());
        if (custBal.compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient customer balance");
        }

        // CORRECT: Customer pays, Bank receives
        customer.setAmount(custBal.subtract(amount));
        bank.setAmount(nullSafe(bank.getAmount()).add(amount));

        customerRepository.save(customer);
        customerRepository.save(bank);

        String txId = UUID.randomUUID().toString();
        saveTransaction(txId, bank.getCustomerNumber(), TransactionType.CREDIT, amount, "Prepayment from " + customerAccount, bankCode, request.getLoanNumber());
        saveTransaction(txId, customer.getCustomerNumber(), TransactionType.DEBIT, amount, "Prepayment to bank", bankCode, request.getLoanNumber());

        return IsoTransferResponse.builder()
                .transactionId(txId)
                .status("SUCCESS")
                .message("Prepayment successful")
                .amount(amount)
                .accountNumber(customerAccount)
                .bankCode(bankCode)
                .build();
    }

    private BigDecimal nullSafe(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val;
    }

    private void saveTransaction(String txId, String acct, TransactionType type, BigDecimal amt, String remarks, String bankCode, String loanNumber) {
        BankTransaction tx = new BankTransaction();
        tx.setTransactionId(txId);
        tx.setAccountNumber(acct);
        tx.setType(type);
        tx.setAmount(amt);
        tx.setParticularRemarks(remarks);
        tx.setBankCode(bankCode);
        tx.setValueDate(LocalDate.now());
        tx.setLoanNumber(loanNumber);
        bankTransactionRepository.save(tx);
    }
}