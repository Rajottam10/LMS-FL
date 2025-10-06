package com.ebooks.systemservice.services.impl;


import com.ebooks.systemservice.dtos.bank.BankAdminDto;
import com.ebooks.systemservice.dtos.bank.BankCreationRequestDto;
import com.ebooks.systemservice.dtos.bank.BankResponseDto;
import com.ebooks.systemservice.entities.Bank;
import com.ebooks.systemservice.entities.BankAdmin;
import com.ebooks.systemservice.entities.Status;
import com.ebooks.systemservice.exceptions.BankAlreadyExistsException;
import com.ebooks.systemservice.exceptions.EmailAlreadyExistsException;
import com.ebooks.systemservice.exceptions.UsernameAlreadyExistsException;
import com.ebooks.systemservice.repositories.AccessGroupRepository;
import com.ebooks.systemservice.repositories.BankAdminRepository;
import com.ebooks.systemservice.repositories.BankRepository;
import com.ebooks.systemservice.repositories.RoleRepository;
import com.ebooks.systemservice.repositories.StatusRepository;
import com.ebooks.systemservice.services.BankService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BankServiceImpl implements BankService {

    private final BankRepository bankRepository;
    private final BankAdminRepository bankAdminRepository;
    private final StatusRepository statusRepository;
    private final AccessGroupRepository accessGroupRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public BankServiceImpl(BankRepository bankRepository,
                           BankAdminRepository bankAdminRepository,
                           StatusRepository statusRepository,
                           AccessGroupRepository accessGroupRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.bankRepository = bankRepository;
        this.bankAdminRepository = bankAdminRepository;
        this.statusRepository = statusRepository;
        this.accessGroupRepository = accessGroupRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public BankResponseDto createBank(BankCreationRequestDto request) {
        Optional<Bank> existingBank = bankRepository.findBankByBankCode(request.getBankCode());
        if (existingBank.isPresent()) {
            throw new BankAlreadyExistsException("Bank with code " + request.getBankCode() + " already exists");
        }

        if (bankAdminRepository.existsByUsername(request.getAdminUsername())) {
            throw new UsernameAlreadyExistsException("Admin username already exists");
        }
        if (bankAdminRepository.existsByEmail(request.getAdminEmail())) {
            throw new EmailAlreadyExistsException("Admin email already exists");
        }

        Status activeStatus = statusRepository.findByName("ACTIVE")
                .orElseThrow(() -> new RuntimeException("ACTIVE status not found"));

        Bank bank = new Bank();
        bank.setName(request.getName());
        bank.setBankCode(request.getBankCode());
        bank.setAddress(request.getAddress());
        bank.setEstablishedDate(request.getEstablishedDate());
        bank.setStatus(activeStatus);
        bank.setIsActive(true);

        Bank savedBank = bankRepository.save(bank);

        BankAdmin bankAdmin = createBankAdmin(request, savedBank, activeStatus);

        BankResponseDto response = new BankResponseDto(
                savedBank.getId(),
                savedBank.getName(),
                savedBank.getBankCode(),
                savedBank.getAddress(),
                savedBank.getEstablishedDate(),
                savedBank.getStatus().getName(),
                savedBank.getCreatedAt()
        );

        response.setBankAdmin(new BankAdminDto(
                bankAdmin.getId(),
                bankAdmin.getUsername(),
                bankAdmin.getEmail(),
                bankAdmin.getFullName()
        ));

        return response;
    }

    private BankAdmin createBankAdmin(BankCreationRequestDto request, Bank bank, Status activeStatus) {
        BankAdmin admin = new BankAdmin();
        admin.setUsername(request.getAdminUsername());
        admin.setPassword(passwordEncoder.encode(request.getAdminPassword()));
        admin.setEmail(request.getAdminEmail());
        admin.setFullName(request.getAdminFullName());
        admin.setBank(bank);
        admin.setStatus(activeStatus);

        return bankAdminRepository.save(admin);
    }

    @Override
    public BankResponseDto getBankByCode(String bankCode) {
        Bank bank = bankRepository.findBankByBankCode(bankCode)
                .orElseThrow(() -> new RuntimeException("Bank not found with code: " + bankCode));

        return convertToDto(bank);
    }

    @Override
    public List<BankResponseDto> getAllBanks() {
        List<Bank> banks = bankRepository.findAll();
        return banks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BankResponseDto updateBank(Long bankId, BankCreationRequestDto request) {
        Bank bank = bankRepository.findById(bankId)
                .orElseThrow(() -> new RuntimeException("Bank not found with id: " + bankId));

        if (!bank.getBankCode().equals(request.getBankCode())) {
            Optional<Bank> existingBankWithNewCode = bankRepository.findBankByBankCode(request.getBankCode());
            if (existingBankWithNewCode.isPresent()) {
                throw new BankAlreadyExistsException("Bank code already exists: " + request.getBankCode());
            }
        }

        bank.setName(request.getName());
        bank.setBankCode(request.getBankCode());
        bank.setAddress(request.getAddress());
        bank.setEstablishedDate(request.getEstablishedDate());

        Bank updatedBank = bankRepository.save(bank);
        return convertToDto(updatedBank);
    }

    @Override
    @Transactional
    public void deleteBank(Long bankId) {
        Bank bank = bankRepository.findById(bankId)
                .orElseThrow(() -> new RuntimeException("Bank not found with id: " + bankId));

        Status deleteStatus = statusRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("DELETE status not found"));
        bank.setIsActive(false);
        bank.setStatus(deleteStatus);
        bankRepository.save(bank);
    }

    private BankResponseDto convertToDto(Bank bank) {
        BankResponseDto dto = new BankResponseDto(
                bank.getId(),
                bank.getName(),
                bank.getBankCode(),
                bank.getAddress(),
                bank.getEstablishedDate(),
                bank.getStatus().getName(),
                bank.getCreatedAt()
        );

        if (bank.getBankAdmins() != null && !bank.getBankAdmins().isEmpty()) {
            BankAdmin primaryAdmin = bank.getBankAdmins().get(0);
            dto.setBankAdmin(new BankAdminDto(
                    primaryAdmin.getId(),
                    primaryAdmin.getUsername(),
                    primaryAdmin.getEmail(),
                    primaryAdmin.getFullName()
            ));
        }

        return dto;
    }
}