
package com.ebooks.systemservice.services.implementation;

import com.ebooks.commonservice.entities.AccessGroup;
import com.ebooks.commonservice.entities.Bank;
import com.ebooks.commonservice.entities.BankAdmin;
import com.ebooks.commonservice.entities.Status;
import com.ebooks.commonservice.repositories.AccessGroupRepository;
import com.ebooks.commonservice.repositories.BankAdminRepository;
import com.ebooks.commonservice.repositories.BankRepository;
import com.ebooks.commonservice.repositories.StatusRepository;
import com.ebooks.systemservice.dtos.BankCreateRequest;
import com.ebooks.systemservice.dtos.BankResponse;
import com.ebooks.systemservice.dtos.BankUpdateRequest;
import com.ebooks.systemservice.mapper.BankMapper;
import com.ebooks.systemservice.services.BankService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Generated;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankServiceImpl implements BankService {
    private final BankRepository bankRepository;
    private final BankAdminRepository bankAdminRepository;
    private final StatusRepository statusRepository;
    private final PasswordEncoder passwordEncoder;
    private final BankMapper bankMapper;
    private final AccessGroupRepository accessGroupRepository;

    @Transactional
    public BankResponse createBankWithBankAdmin(BankCreateRequest bankCreateRequest) {
        if (this.bankRepository.existsByBankCode(bankCreateRequest.getBankCode())) {
            throw new RuntimeException("Already bank exists with the same bank code.");
        } else if (this.bankAdminRepository.existsByUsername(bankCreateRequest.getAdmin().getUsername())) {
            throw new RuntimeException("An bank admin already exists with the same username.");
        } else {
            AccessGroup accessGroup = (AccessGroup)this.accessGroupRepository.findById(bankCreateRequest.getAdmin().getAccessGroupId()).orElseThrow(() -> new IllegalArgumentException("Access group not found."));
            if (!accessGroup.getType().equals("BANK")) {
                throw new RuntimeException("You need access group of type BANK only to assign to Bank Admin.");
            } else {
                Status activeStatus = this.statusRepository.findByName("ACTIVE");
                if (activeStatus == null) {
                    throw new RuntimeException("Status ACTIVE does not exist.");
                } else {
                    Bank bank = this.bankMapper.toBank(bankCreateRequest);
                    bank.setCreatedAt(LocalDateTime.now());
                    bank.setStatus(activeStatus);
                    BankAdmin bankAdmin = this.bankMapper.toBankAdmin(bankCreateRequest);
                    bankAdmin.setPassword(this.passwordEncoder.encode(bankCreateRequest.getAdmin().getPassword()));
                    bankAdmin.setStatus(activeStatus);
                    bankAdmin.setAccessGroup(accessGroup);
                    bankAdmin.setBank(bank);
                    bank.setBankAdmin(bankAdmin);
                    this.bankRepository.save(bank);
                    return this.bankMapper.toBankResponse(bank);
                }
            }
        }
    }

    @Transactional(
            readOnly = true
    )
    public List<BankResponse> getAllBanksWithBankAdmin() {
        List<Bank> banks = this.bankRepository.findAll();
        Stream var10000 = banks.stream();
        BankMapper var10001 = this.bankMapper;
        Objects.requireNonNull(var10001);
        return (List)var10000.map(var10001::toBankResponse).collect(Collectors.toList());
    }

    @Transactional
    public void deleteBankByBankCode(String bankCode) {
        Bank bank = this.bankRepository.findByBankCode(bankCode);
        if (bank == null) {
            throw new RuntimeException("Bank does not exist with the bankCode: " + bankCode);
        } else {
            BankAdmin bankAdmin = bank.getBankAdmin();
            if (bankAdmin != null) {
                bankAdmin.setBank((Bank)null);
                this.bankAdminRepository.save(bankAdmin);
            }

            this.bankRepository.delete(bank);
        }
    }

    @Transactional
    public BankResponse updateBankWithBankAdmin(String bankCode, BankUpdateRequest bankUpdateRequest) {
        Bank existingBank = this.bankRepository.findByBankCode(bankCode);
        if (existingBank == null) {
            throw new RuntimeException("Bank not found with bankCode: " + bankCode);
        } else {
            existingBank.setName(bankUpdateRequest.getName());
            existingBank.setAddress(bankUpdateRequest.getAddress());
            existingBank.setIsActive(bankUpdateRequest.getIsActive());
            existingBank.setUpdatedAt(LocalDateTime.now());
            AccessGroup accessGroup = (AccessGroup)this.accessGroupRepository.findById(bankUpdateRequest.getAdmin().getAccessGroupId()).orElseThrow(() -> new IllegalArgumentException("Access group not found."));
            if (!accessGroup.getType().equals("BANK")) {
                throw new RuntimeException("You need access group of type BANK only to assign to Bank Admin.");
            } else {
                BankAdmin existingAdmin = existingBank.getBankAdmin();
                if (existingAdmin != null) {
                    existingAdmin.setFullName(bankUpdateRequest.getAdmin().getFullName());
                    existingAdmin.setEmail(bankUpdateRequest.getAdmin().getEmail());
                    existingAdmin.setPassword(this.passwordEncoder.encode(bankUpdateRequest.getAdmin().getPassword()));
                    existingAdmin.setAccessGroup(accessGroup);
                }

                this.bankRepository.save(existingBank);
                return this.bankMapper.toBankResponse(existingBank);
            }
        }
    }

    public BankResponse findByBankCode(String bankCode) {
        return this.bankMapper.toBankResponse(this.bankRepository.findByBankCode(bankCode));
    }

    @Generated
    public BankServiceImpl(final BankRepository bankRepository, final BankAdminRepository bankAdminRepository, final StatusRepository statusRepository, final PasswordEncoder passwordEncoder, final BankMapper bankMapper, final AccessGroupRepository accessGroupRepository) {
        this.bankRepository = bankRepository;
        this.bankAdminRepository = bankAdminRepository;
        this.statusRepository = statusRepository;
        this.passwordEncoder = passwordEncoder;
        this.bankMapper = bankMapper;
        this.accessGroupRepository = accessGroupRepository;
    }
}
