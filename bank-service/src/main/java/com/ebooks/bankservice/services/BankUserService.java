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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Generated;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BankUserService {
    private final BankUserRepository bankUserRepository;
    private final BankUserMapper bankUserMapper;
    private final AccessGroupRepository accessGroupRepository;
    private final PasswordEncoder passwordEncoder;
    private final StatusRepository statusRepository;

    public List<BankUserResponse> getAllBankUsers(Bank bank, int page, int size) {
        if (bank == null) {
            throw new IllegalArgumentException("Bank cannot be null for this operation");
        } else {
            Pageable pageable = PageRequest.of(page, size);
            Stream var10000 = this.bankUserRepository.findBankUserByBank(bank, pageable).getContent().stream();
            BankUserMapper var10001 = this.bankUserMapper;
            Objects.requireNonNull(var10001);
            return var10000.map(var10001::toResponse).toList();
        }
    }

    public BankUserResponse createBankUser(BankUserRequest bankUserRequest, Bank bank) {
        Optional<AccessGroup> accessGroupOptional = this.accessGroupRepository.findById(bankUserRequest.getAccessGroupId());
        if (accessGroupOptional.isEmpty()) {
            throw new RuntimeException("No access group found with id- " + bankUserRequest.getAccessGroupId());
        } else {
            AccessGroup accessGroup = (AccessGroup)accessGroupOptional.get();
            if (!accessGroup.getBank().getId().equals(bank.getId())) {
                throw new RuntimeException("You are not allowed to access this resource.");
            } else {
                BankUser bankUser = this.bankUserMapper.toEntity(bankUserRequest);
                bankUser.setAccessGroup(accessGroup);
                bankUser.setBank(bank);
                bankUser.setCreatedAt(LocalDateTime.now());
                bankUser.setPassword(this.passwordEncoder.encode(bankUserRequest.getPassword()));
                Status activeStatus = this.statusRepository.findByName("ACTIVE");
                if (activeStatus == null) {
                    throw new RuntimeException("Status ACTIVE does not exist.");
                } else {
                    bankUser.setStatus(activeStatus);
                    return this.bankUserMapper.toResponse((BankUser)this.bankUserRepository.save(bankUser));
                }
            }
        }
    }

    public void deleteBankUserById(Long bankUserId, Bank bank) {
        if (bank == null) {
            throw new IllegalArgumentException("Bank cannot be null for this operation");
        } else {
            Optional<BankUser> bankUserOptional = this.bankUserRepository.findById(bankUserId);
            if (bankUserOptional.isEmpty()) {
                throw new RuntimeException("No bank user found with id " + bankUserId);
            } else {
                BankUser bankUser = (BankUser)bankUserOptional.get();
                if (!bankUser.getBank().getId().equals(bank.getId())) {
                    throw new RuntimeException("You are not allowed to access this resource.");
                } else {
                    this.bankUserRepository.delete(bankUser);
                }
            }
        }
    }

    public BankUserResponse getBankUserById(Long bankUserId, Bank bank) {
        if (bank == null) {
            throw new IllegalArgumentException("Bank cannot be null for this operation");
        } else {
            Optional<BankUser> bankUserOptional = this.bankUserRepository.findById(bankUserId);
            if (bankUserOptional.isEmpty()) {
                throw new RuntimeException("No bank user found with id " + bankUserId);
            } else {
                BankUser bankUser = (BankUser)bankUserOptional.get();
                if (!bankUser.getBank().getId().equals(bank.getId())) {
                    throw new RuntimeException("You are not allowed to access this resource.");
                } else {
                    return this.bankUserMapper.toResponse(bankUser);
                }
            }
        }
    }

    public void blockBankUser(Long userId, Bank bank) {
        BankUser bankUser = (BankUser)this.bankUserRepository.findById(userId).orElseThrow(() -> new RuntimeException("No bank user found with id - " + userId));
        if (!bankUser.getBank().getId().equals(bank.getId())) {
            throw new RuntimeException("You are not allowed to access this resource.");
        } else {
            Status blockStatus = this.statusRepository.findByName("BLOCK");
            if (blockStatus == null) {
                throw new RuntimeException("Status BLOCK does not exist.");
            } else {
                bankUser.setStatus(blockStatus);
                bankUser.setUpdatedAt(LocalDateTime.now());
                this.bankUserRepository.save(bankUser);
            }
        }
    }

    public void unblockBankUser(Long userId, Bank bank) {
        BankUser bankUser = (BankUser)this.bankUserRepository.findById(userId).orElseThrow(() -> new RuntimeException("No bank user found with id - " + userId));
        if (!bankUser.getBank().getId().equals(bank.getId())) {
            throw new RuntimeException("You are not allowed to access this resource.");
        } else if (!"BLOCK".equalsIgnoreCase(bankUser.getStatus().getName())) {
            throw new RuntimeException("Only blocked users can be unblocked.");
        } else {
            Status activeStatus = this.statusRepository.findByName("ACTIVE");
            if (activeStatus == null) {
                throw new RuntimeException("Status ACTIVE does not exist.");
            } else {
                bankUser.setStatus(activeStatus);
                bankUser.setUpdatedAt(LocalDateTime.now());
                this.bankUserRepository.save(bankUser);
            }
        }
    }

    public void deleteBankUser(Long userId, Bank bank) {
        BankUser bankUser = (BankUser)this.bankUserRepository.findById(userId).orElseThrow(() -> new RuntimeException("No bank user found with id - " + userId));
        if (!bankUser.getBank().getId().equals(bank.getId())) {
            throw new RuntimeException("You are not allowed to access this resource.");
        } else {
            Status deletedStatus = this.statusRepository.findByName("DELETE");
            if (deletedStatus == null) {
                throw new RuntimeException("Status DELETE does not exist.");
            } else {
                bankUser.setStatus(deletedStatus);
                bankUser.setUpdatedAt(LocalDateTime.now());
                this.bankUserRepository.save(bankUser);
            }
        }
    }

    public BankUserResponse updateBankUser(BankUserUpdateRequest bankUserRequest, Long bankUserId, Bank bank) {
        BankUser existingBankUser = (BankUser)this.bankUserRepository.findById(bankUserId).orElseThrow(() -> new RuntimeException("Bank user not found with id - " + bankUserId));
        AccessGroup accessGroup = (AccessGroup)this.accessGroupRepository.findById(bankUserRequest.getAccessGroupId()).orElseThrow(() -> new RuntimeException("No access group found with id - " + bankUserRequest.getAccessGroupId()));
        if (!accessGroup.getBank().getId().equals(bank.getId())) {
            throw new RuntimeException("You are not allowed to access this resource.");
        } else {
            existingBankUser.setFullName(bankUserRequest.getFullName());
            existingBankUser.setMobileNumber(bankUserRequest.getMobileNumber());
            existingBankUser.setAddress(bankUserRequest.getAddress());
            existingBankUser.setAccessGroup(accessGroup);
            existingBankUser.setBank(bank);
            if (bankUserRequest.getPassword() != null && !bankUserRequest.getPassword().isEmpty()) {
                existingBankUser.setPassword(this.passwordEncoder.encode(bankUserRequest.getPassword()));
            }

            existingBankUser.setUpdatedAt(LocalDateTime.now());
            BankUser updatedBankUser = (BankUser)this.bankUserRepository.save(existingBankUser);
            return this.bankUserMapper.toResponse(updatedBankUser);
        }
    }

    @Generated
    public BankUserService(final BankUserRepository bankUserRepository, final BankUserMapper bankUserMapper, final AccessGroupRepository accessGroupRepository, final PasswordEncoder passwordEncoder, final StatusRepository statusRepository) {
        this.bankUserRepository = bankUserRepository;
        this.bankUserMapper = bankUserMapper;
        this.accessGroupRepository = accessGroupRepository;
        this.passwordEncoder = passwordEncoder;
        this.statusRepository = statusRepository;
    }
}
