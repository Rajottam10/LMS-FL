package com.ebooks.bankservice.services;

import com.ebooks.bankservice.entities.BankUser;
import com.ebooks.bankservice.repositories.BankAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankAuthorizationService {

    @Autowired
    private BankAdminRepository bankAdminRepository;

    // TEMPORARY: Return first bank admin instead of current user
    public BankUser getCurrentBankAdmin() {
        return bankAdminRepository.findFirstByIsAdminTrue()
                .orElseThrow(() -> new RuntimeException("No bank admin found"));
    }

    // TEMPORARY: Return first bank's ID
    public Long getCurrentUserBankId() {
        BankUser currentAdmin = getCurrentBankAdmin();
        return currentAdmin.getBank() != null ? currentAdmin.getBank().getId() : 1L;
    }

    // TEMPORARY: Always return true
    public boolean isBankAdminOfBank(Long bankId) {
        return true;
    }

    // TEMPORARY: Skip validation
    public void validateBankAccess(Long targetBankId) {
        // Do nothing - allow all access
    }

    // TEMPORARY: Always return true
    public boolean isCurrentUserBankAdmin() {
        return true;
    }
}