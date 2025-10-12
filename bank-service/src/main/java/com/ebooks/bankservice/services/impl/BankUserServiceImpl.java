package com.ebooks.bankservice.services.impl;

import com.ebooks.bankservice.dtos.BankUserRequestDto;
import com.ebooks.bankservice.dtos.BankUserResponseDto;
import com.ebooks.bankservice.entities.Bank;
import com.ebooks.bankservice.entities.BankUser;
import com.ebooks.bankservice.repositories.BankUserRepository;
import com.ebooks.bankservice.services.BankUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BankUserServiceImpl implements BankUserService {

    @Autowired
    private BankUserRepository bankUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public BankUserResponseDto createBankUser(BankUserRequestDto request) {
        // Check if username or email already exists
        if (bankUserRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }
        if (bankUserRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        // Get current user's bank to ensure new user is created in the same bank
        Bank currentUserBank = getCurrentUserBank();

        BankUser bankUser = new BankUser();
        bankUser.setUsername(request.getUsername());
        bankUser.setEmail(request.getEmail());
        bankUser.setFullName(request.getFullName());
        bankUser.setPassword(passwordEncoder.encode(request.getPassword()));
        bankUser.setBank(currentUserBank); // Use current user's bank
        bankUser.setStatus("ACTIVE");
        bankUser.setIsAdmin(request.getIsAdmin());
        bankUser.setAccessGroup(request.getAccessGroup());

        BankUser savedUser = bankUserRepository.save(bankUser);
        return mapToBankUserResponseDto(savedUser);
    }

    @Override
    public BankUserResponseDto getBankUserById(Long id) {
        BankUser bankUser = bankUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bank user not found with id: " + id));

        // Ensure the current user can only access users from their own bank
        validateSameBankAccess(bankUser);

        return mapToBankUserResponseDto(bankUser);
    }

    @Override
    public List<BankUserResponseDto> getBankUsersByCurrentBank() {
        Bank currentUserBank = getCurrentUserBank();
        List<BankUser> bankUsers = bankUserRepository.findByBank(currentUserBank);
        return bankUsers.stream()
                .map(this::mapToBankUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BankUserResponseDto> getBankUsersByCurrentBank(Pageable pageable) {
        Bank currentUserBank = getCurrentUserBank();
        Page<BankUser> bankUsers = bankUserRepository.findByBank(currentUserBank, pageable);
        return bankUsers.map(this::mapToBankUserResponseDto);
    }

    @Override
    public BankUserResponseDto updateBankUser(Long id, BankUserRequestDto request) {
        BankUser bankUser = bankUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bank user not found with id: " + id));

        // Ensure the current user can only update users from their own bank
        validateSameBankAccess(bankUser);

        // Check username uniqueness if changed
        if (!bankUser.getUsername().equals(request.getUsername()) &&
                bankUserRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }

        // Check email uniqueness if changed
        if (!bankUser.getEmail().equals(request.getEmail()) &&
                bankUserRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        bankUser.setUsername(request.getUsername());
        bankUser.setEmail(request.getEmail());
        bankUser.setFullName(request.getFullName());
        bankUser.setIsAdmin(request.getIsAdmin());
        bankUser.setAccessGroup(request.getAccessGroup());

        // Only update password if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            bankUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        BankUser updatedUser = bankUserRepository.save(bankUser);
        return mapToBankUserResponseDto(updatedUser);
    }

    @Override
    public void deleteBankUser(Long id) {
        BankUser bankUser = bankUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bank user not found with id: " + id));

        // Ensure the current user can only delete users from their own bank
        validateSameBankAccess(bankUser);

        bankUser.setStatus("DELETED");
        bankUserRepository.save(bankUser);
    }

    @Override
    public void blockBankUser(Long id) {
        BankUser bankUser = bankUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bank user not found with id: " + id));

        // Ensure the current user can only block users from their own bank
        validateSameBankAccess(bankUser);

        bankUser.setStatus("BLOCKED");
        bankUserRepository.save(bankUser);
    }

    @Override
    public void unblockBankUser(Long id) {
        BankUser bankUser = bankUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bank user not found with id: " + id));

        // Ensure the current user can only unblock users from their own bank
        validateSameBankAccess(bankUser);

        bankUser.setStatus("ACTIVE");
        bankUserRepository.save(bankUser);
    }

    // Helper method to get current user's bank
    private Bank getCurrentUserBank() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        BankUser currentUser = bankUserRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        if (currentUser.getBank() == null) {
            throw new RuntimeException("Current user is not associated with any bank");
        }

        return currentUser.getBank();
    }

    // Helper method to validate that the current user can only access users from their own bank
    private void validateSameBankAccess(BankUser targetUser) {
        Bank currentUserBank = getCurrentUserBank();

        if (targetUser.getBank() == null || !currentUserBank.getId().equals(targetUser.getBank().getId())) {
            throw new RuntimeException("Access denied: You can only manage users from your own bank");
        }
    }

    private BankUserResponseDto mapToBankUserResponseDto(BankUser bankUser) {
        BankUserResponseDto dto = new BankUserResponseDto();
        dto.setId(bankUser.getId());
        dto.setUsername(bankUser.getUsername());
        dto.setEmail(bankUser.getEmail());
        dto.setFullName(bankUser.getFullName());
        dto.setStatus(bankUser.getStatus());
        dto.setIsAdmin(bankUser.getIsAdmin());
        dto.setCreatedAt(bankUser.getCreatedAt());
        dto.setUpdatedAt(bankUser.getUpdatedAt());

        if (bankUser.getBank() != null) {
            dto.setBankId(bankUser.getBank().getId());
            dto.setBankName(bankUser.getBank().getName());
        }

        if (bankUser.getAccessGroup() != null) {
            dto.setAccessGroupId(bankUser.getAccessGroup().getId());
            dto.setAccessGroupName(bankUser.getAccessGroup().getName());
        }

        return dto;
    }
}