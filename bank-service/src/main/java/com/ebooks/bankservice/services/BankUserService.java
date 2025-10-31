package com.ebooks.bankservice.services;

import com.ebooks.bankservice.dtos.BankUserRequest;
import com.ebooks.bankservice.dtos.BankUserResponse;
import com.ebooks.bankservice.dtos.BankUserUpdateRequest;
import com.ebooks.bankservice.entities.BankUser;
import com.ebooks.bankservice.mapper.BankUserMapper;
import com.ebooks.bankservice.repositories.BankUserRepository;
import com.ebooks.commonservice.entities.AccessGroup;
import com.ebooks.commonservice.entities.Bank;
import com.ebooks.commonservice.entities.Status;
import com.ebooks.commonservice.repositories.AccessGroupRepository;
import com.ebooks.commonservice.repositories.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BankUserService {
    private final BankUserRepository bankUserRepository;
    private final BankUserMapper bankUserMapper;
    private final AccessGroupRepository accessGroupRepository;
    private final PasswordEncoder passwordEncoder;
    private final StatusRepository statusRepository;

    public List<BankUserResponse> getAllBankUsers(Bank bank, int page, int size) {
        if (bank == null) {
            throw new IllegalArgumentException("Bank cannot be null for this operation");
        }
        Pageable pageable = PageRequest.of(page, size);
        return bankUserRepository.findBankUserByBank(bank, pageable).getContent()
                .stream().map(bankUserMapper::toResponse).toList();
    }

    public BankUserResponse createBankUser(BankUserRequest bankUserRequest, Bank bank) {
        Optional<AccessGroup> accessGroupOptional = accessGroupRepository.findById(bankUserRequest.getAccessGroupId());
        if (accessGroupOptional.isEmpty()) {
            throw new RuntimeException("No access group found with id- " + bankUserRequest.getAccessGroupId());
        }
        AccessGroup accessGroup = accessGroupOptional.get();
        if (!accessGroup.getBank().getId().equals(bank.getId())) {
            throw new RuntimeException("You are not allowed to access this resource.");
        }
        BankUser bankUser = bankUserMapper.toEntity(bankUserRequest);
        bankUser.setAccessGroup(accessGroup);
        bankUser.setBank(bank);
        bankUser.setCreatedAt(LocalDateTime.now());
        bankUser.setPassword(passwordEncoder.encode(bankUserRequest.getPassword()));
        Status activeStatus = statusRepository.findByName("ACTIVE");
        if (activeStatus == null) {
            throw new RuntimeException("Status ACTIVE does not exist.");
        }
        bankUser.setStatus(activeStatus);
        return bankUserMapper.toResponse(bankUserRepository.save(bankUser));
    }

    public void deleteBankUserById(Long bankUserId, Bank bank) {
        if (bank == null) {
            throw new IllegalArgumentException("Bank cannot be null for this operation");
        }
        Optional<BankUser> bankUserOptional = bankUserRepository.findById(bankUserId);
        if (bankUserOptional.isEmpty()) {
            throw new RuntimeException("No bank user found with id " + bankUserId);
        }
        BankUser bankUser = bankUserOptional.get();
        if (!bankUser.getBank().getId().equals(bank.getId())) {
            throw new RuntimeException("You are not allowed to access this resource.");
        }
        bankUserRepository.delete(bankUser);
    }

    public BankUserResponse getBankUserById(Long bankUserId, Bank bank) {
        if (bank == null) {
            throw new IllegalArgumentException("Bank cannot be null for this operation");
        }
        Optional<BankUser> bankUserOptional = bankUserRepository.findById(bankUserId);
        if (bankUserOptional.isEmpty()) {
            throw new RuntimeException("No bank user found with id " + bankUserId);
        }
        BankUser bankUser = bankUserOptional.get();
        if (!bankUser.getBank().getId().equals(bank.getId())) {
            throw new RuntimeException("You are not allowed to access this resource.");
        }
        return bankUserMapper.toResponse(bankUser);
    }

    public void blockBankUser(Long userId, Bank bank) {
        BankUser bankUser = bankUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("No bank user found with id - " + userId));

        if (!bankUser.getBank().getId().equals(bank.getId())) {
            throw new RuntimeException("You are not allowed to access this resource.");
        }

        Status blockStatus = statusRepository.findByName("BLOCK");
        if (blockStatus == null) {
            throw new RuntimeException("Status BLOCK does not exist.");
        }

        bankUser.setStatus(blockStatus);
        bankUser.setUpdatedAt(LocalDateTime.now());
        bankUserRepository.save(bankUser);
    }

    public void unblockBankUser(Long userId, Bank bank) {
        BankUser bankUser = bankUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("No bank user found with id - " + userId));

        if (!bankUser.getBank().getId().equals(bank.getId())) {
            throw new RuntimeException("You are not allowed to access this resource.");
        }

        if (!"BLOCK".equalsIgnoreCase(bankUser.getStatus().getName())) {
            throw new RuntimeException("Only blocked users can be unblocked.");
        }

        Status activeStatus = statusRepository.findByName("ACTIVE");
        if (activeStatus == null) {
            throw new RuntimeException("Status ACTIVE does not exist.");
        }

        bankUser.setStatus(activeStatus);
        bankUser.setUpdatedAt(LocalDateTime.now());
        bankUserRepository.save(bankUser);
    }

    public void deleteBankUser(Long userId, Bank bank) {
        BankUser bankUser = bankUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("No bank user found with id - " + userId));

        if (!bankUser.getBank().getId().equals(bank.getId())) {
            throw new RuntimeException("You are not allowed to access this resource.");
        }

        Status deletedStatus = statusRepository.findByName("DELETE");
        if (deletedStatus == null) {
            throw new RuntimeException("Status DELETE does not exist.");
        }

        bankUser.setStatus(deletedStatus);
        bankUser.setUpdatedAt(LocalDateTime.now());
        bankUserRepository.save(bankUser);
    }

    public BankUserResponse updateBankUser(BankUserUpdateRequest bankUserRequest, Long bankUserId, Bank bank) {
        BankUser existingBankUser = bankUserRepository.findById(bankUserId)
                .orElseThrow(() -> new RuntimeException("Bank user not found with id - " + bankUserId));

        AccessGroup accessGroup = accessGroupRepository.findById(bankUserRequest.getAccessGroupId())
                .orElseThrow(() -> new RuntimeException("No access group found with id - " + bankUserRequest.getAccessGroupId()));

        if (!accessGroup.getBank().getId().equals(bank.getId())) {
            throw new RuntimeException("You are not allowed to access this resource.");
        }

        existingBankUser.setFullName(bankUserRequest.getFullName());
        existingBankUser.setMobileNumber(bankUserRequest.getMobileNumber());
        existingBankUser.setAddress(bankUserRequest.getAddress());
        existingBankUser.setAccessGroup(accessGroup);
        existingBankUser.setBank(bank);

        if (bankUserRequest.getPassword() != null && !bankUserRequest.getPassword().isEmpty()) {
            existingBankUser.setPassword(passwordEncoder.encode(bankUserRequest.getPassword()));
        }
        existingBankUser.setUpdatedAt(LocalDateTime.now());

        BankUser updatedBankUser = bankUserRepository.save(existingBankUser);
        return bankUserMapper.toResponse(updatedBankUser);
    }

}
