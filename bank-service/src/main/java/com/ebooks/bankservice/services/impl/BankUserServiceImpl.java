package com.ebooks.bankservice.services.impl;

import com.ebooks.bankservice.dtos.BankUserRequestDto;
import com.ebooks.bankservice.dtos.BankUserResponseDto;
import com.ebooks.bankservice.entities.BankUser;
import com.ebooks.bankservice.repositories.BankAdminRepository;
import com.ebooks.bankservice.repositories.BankRepository;
import com.ebooks.bankservice.services.BankAuthorizationService;
import com.ebooks.bankservice.services.BankUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankUserServiceImpl implements BankUserService {

    @Autowired
    private BankAdminRepository bankAdminRepository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BankAuthorizationService bankAuthorizationService;

    @Override
    @Transactional
    public BankUserResponseDto createBankUser(BankUserRequestDto requestDTO) {
        BankUser currentAdmin = bankAuthorizationService.getCurrentBankAdmin();

        // Check if current user has admin privileges
        if (!Boolean.TRUE.equals(currentAdmin.getIsAdmin())) {
            throw new AccessDeniedException("Only bank admins can create bank users");
        }

        if (bankAdminRepository.existsByUsername(requestDTO.getUsername())) {
            throw new RuntimeException("Username already exists: " + requestDTO.getUsername());
        }
        if (bankAdminRepository.existsByEmail(requestDTO.getEmail())) {
            throw new RuntimeException("Email already exists: " + requestDTO.getEmail());
        }

        BankUser bankUser = new BankUser();
        bankUser.setEmail(requestDTO.getEmail());
        bankUser.setUsername(requestDTO.getUsername());
        bankUser.setFullName(requestDTO.getFullName());
        bankUser.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        bankUser.setBank(currentAdmin.getBank());
        bankUser.setStatus("ACTIVE");
        bankUser.setIsAdmin(false);
        bankUser.setAccessGroupId(requestDTO.getAccessGroupId());

        BankUser savedUser = bankAdminRepository.save(bankUser);
        return convertToDTO(savedUser);
    }

    @Override
    public List<BankUserResponseDto> getBankUsersByCurrentBank() {
        BankUser currentAdmin = bankAuthorizationService.getCurrentBankAdmin();
        Long currentBankId = currentAdmin.getBank().getId();

        List<BankUser> bankUsers = bankAdminRepository.findByBankIdAndIsAdminFalse(currentBankId);
        return bankUsers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BankUserResponseDto> getBankUsersByCurrentBank(Pageable pageable) {
        BankUser currentAdmin = bankAuthorizationService.getCurrentBankAdmin();
        Long currentBankId = currentAdmin.getBank().getId();

        Page<BankUser> bankUsersPage = bankAdminRepository.findByBankIdAndIsAdminFalse(currentBankId, pageable);
        return bankUsersPage.map(this::convertToDTO);
    }

    @Override
    public BankUserResponseDto getBankUserById(Long userId) {
        BankUser currentAdmin = bankAuthorizationService.getCurrentBankAdmin();
        BankUser bankUser = bankAdminRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Bank user not found with id: " + userId));

        // Verify the user belongs to the same bank
        if (!bankUser.getBank().getId().equals(currentAdmin.getBank().getId())) {
            throw new AccessDeniedException("Not authorized to access this user");
        }

        return convertToDTO(bankUser);
    }

    @Override
    @Transactional
    public BankUserResponseDto updateBankUser(Long userId, BankUserRequestDto requestDTO) {
        BankUser currentAdmin = bankAuthorizationService.getCurrentBankAdmin();
        BankUser bankUser = bankAdminRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Bank user not found with id: " + userId));

        // Verify the user belongs to the same bank and is not admin
        if (!bankUser.getBank().getId().equals(currentAdmin.getBank().getId()) ||
                Boolean.TRUE.equals(bankUser.getIsAdmin())) {
            throw new AccessDeniedException("Not authorized to update this user");
        }

        bankUser.setFullName(requestDTO.getFullName());
        bankUser.setAccessGroupId(requestDTO.getAccessGroupId());

        if (requestDTO.getPassword() != null && !requestDTO.getPassword().trim().isEmpty()) {
            bankUser.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        }

        BankUser updatedUser = bankAdminRepository.save(bankUser);
        return convertToDTO(updatedUser);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('DELETE_USER')")
    public void deleteBankUser(Long userId) {
        BankUser currentAdmin = bankAuthorizationService.getCurrentBankAdmin();
        BankUser bankUser = bankAdminRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Bank user not found with id: " + userId));

        // Verify the user belongs to the same bank and is not admin
        if (!bankUser.getBank().getId().equals(currentAdmin.getBank().getId())) {
            throw new AccessDeniedException("Not authorized to delete users from other banks");
        }

        if (Boolean.TRUE.equals(bankUser.getIsAdmin())) {
            throw new AccessDeniedException("Not authorized to delete admin users");
        }

        // Check if user is already deleted
        if ("DELETED".equals(bankUser.getStatus())) {
            throw new RuntimeException("User is already deleted");
        }

        // Allow deletion of both active and blocked users
        if (!"ACTIVE".equals(bankUser.getStatus()) && !"BLOCKED".equals(bankUser.getStatus())) {
            throw new RuntimeException("Cannot delete user with status: " + bankUser.getStatus());
        }

        bankUser.setStatus("DELETED");
        bankAdminRepository.save(bankUser);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('BLOCK_USER')")
    public void blockBankUser(Long userId) {
        BankUser currentAdmin = bankAuthorizationService.getCurrentBankAdmin();
        BankUser bankUser = bankAdminRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Bank user not found with id: " + userId));

        // Verify the user belongs to the same bank and is not admin
        if (!bankUser.getBank().getId().equals(currentAdmin.getBank().getId())) {
            throw new AccessDeniedException("Not authorized to block users from other banks");
        }

        if (Boolean.TRUE.equals(bankUser.getIsAdmin())) {
            throw new AccessDeniedException("Not authorized to block admin users");
        }

        // Check if user is already blocked or deleted
        if ("BLOCKED".equals(bankUser.getStatus())) {
            throw new RuntimeException("User is already blocked");
        }
        if ("DELETED".equals(bankUser.getStatus())) {
            throw new RuntimeException("Cannot block a deleted user");
        }

        // Only active users can be blocked
        if (!"ACTIVE".equals(bankUser.getStatus())) {
            throw new RuntimeException("Only active users can be blocked");
        }

        bankUser.setStatus("BLOCKED");
        bankAdminRepository.save(bankUser);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('UNBLOCK_USER')")
    public void unblockBankUser(Long userId) {
        BankUser currentAdmin = bankAuthorizationService.getCurrentBankAdmin();
        BankUser bankUser = bankAdminRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Bank user not found with id: " + userId));

        // Verify the user belongs to the same bank and is not admin
        if (!bankUser.getBank().getId().equals(currentAdmin.getBank().getId())) {
            throw new AccessDeniedException("Not authorized to unblock users from other banks");
        }

        if (Boolean.TRUE.equals(bankUser.getIsAdmin())) {
            throw new AccessDeniedException("Not authorized to unblock admin users");
        }

        // ONLY blocked users can be unblocked (strict requirement)
        if (!"BLOCKED".equals(bankUser.getStatus())) {
            throw new RuntimeException("Only blocked users can be unblocked. Current status: " + bankUser.getStatus());
        }

        bankUser.setStatus("ACTIVE");
        bankAdminRepository.save(bankUser);
    }

    private BankUserResponseDto convertToDTO(BankUser bankUser) {
        BankUserResponseDto dto = new BankUserResponseDto();
        dto.setId(bankUser.getId());
        dto.setEmail(bankUser.getEmail());
        dto.setUsername(bankUser.getUsername());
        dto.setFullName(bankUser.getFullName());
        dto.setStatus(bankUser.getStatus());
        dto.setBankId(bankUser.getBank().getId());
        dto.setBankName(bankUser.getBank().getName());
        dto.setAccessGroupId(bankUser.getAccessGroupId());
        dto.setCreatedAt(bankUser.getCreatedAt());
        return dto;
    }
}