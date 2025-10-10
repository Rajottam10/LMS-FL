package com.ebooks.bankservice.services.impl;

import com.ebooks.bankservice.dtos.BankAdminDto;
import com.ebooks.bankservice.dtos.BankCreationRequestDto;
import com.ebooks.bankservice.dtos.BankResponseDto;
import com.ebooks.bankservice.entities.Bank;
import com.ebooks.bankservice.entities.BankUser;
import com.ebooks.bankservice.repositories.BankAdminRepository;
import com.ebooks.bankservice.repositories.BankRepository;
import com.ebooks.bankservice.services.BankService;
import com.ebooks.commonservice.exceptions.BankAlreadyExistsException;
import com.ebooks.commonservice.exceptions.EmailAlreadyExistsException;
import com.ebooks.commonservice.exceptions.UsernameAlreadyExistsException;
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
    private final PasswordEncoder passwordEncoder;

    public BankServiceImpl(BankRepository bankRepository,
                           BankAdminRepository bankAdminRepository,
                           PasswordEncoder passwordEncoder) {
        this.bankRepository = bankRepository;
        this.bankAdminRepository = bankAdminRepository;
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

        Bank bank = new Bank();
        bank.setName(request.getName());
        bank.setBankCode(request.getBankCode());
        bank.setAddress(request.getAddress());
        bank.setEstablishedDate(request.getEstablishedDate());
        bank.setStatus("ACTIVE");
        bank.setIsActive(true);


        Bank savedBank = bankRepository.save(bank);

        BankUser bankUser = createBankAdmin(request, savedBank);

        BankResponseDto response = new BankResponseDto(
                savedBank.getId(),
                savedBank.getName(),
                savedBank.getBankCode(),
                savedBank.getAddress(),
                savedBank.getEstablishedDate(),
                savedBank.getStatus(),
                savedBank.getCreatedAt()
        );

        response.setBankAdmin(new BankAdminDto(
                bankUser.getId(),
                bankUser.getUsername(),
                bankUser.getEmail(),
                bankUser.getFullName()
        ));

        return response;
    }

    private BankUser createBankAdmin(BankCreationRequestDto request, Bank bank) {
        BankUser admin = new BankUser();
        admin.setUsername(request.getAdminUsername());
        admin.setPassword(passwordEncoder.encode(request.getAdminPassword()));
        admin.setEmail(request.getAdminEmail());
        admin.setFullName(request.getAdminFullName());
        admin.setBank(bank);
        admin.setStatus("ACTIVE");
        admin.setIsAdmin(Boolean.TRUE);

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

        bank.setIsActive(false);
        bank.setStatus("DELETED");
        bankRepository.save(bank);
    }

    private BankResponseDto convertToDto(Bank bank) {
        BankResponseDto dto = new BankResponseDto(
                bank.getId(),
                bank.getName(),
                bank.getBankCode(),
                bank.getAddress(),
                bank.getEstablishedDate(),
                bank.getStatus(),
                bank.getCreatedAt()
        );

        if (bank.getBankUsers() != null && !bank.getBankUsers().isEmpty()) {
            BankUser primaryAdmin = bank.getBankUsers().get(0);
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
