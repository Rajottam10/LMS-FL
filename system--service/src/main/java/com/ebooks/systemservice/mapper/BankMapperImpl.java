//package com.ebooks.systemservice.mapper;
//
//import com.ebooks.commonservice.entities.AccessGroup;
//import com.ebooks.commonservice.entities.Bank;
//import com.ebooks.commonservice.entities.BankAdmin;
//import com.ebooks.systemservice.dtos.BankCreateRequest;
//import com.ebooks.systemservice.dtos.BankResponse;
//import org.springframework.stereotype.Component;
//
//@Component
//public class BankMapperImpl implements BankMapper {
//    public Bank toBank(BankCreateRequest request) {
//        if (request == null) {
//            return null;
//        } else {
//            Bank bank = new Bank();
//            bank.setBankCode(request.getBankCode());
//            bank.setName(request.getName());
//            bank.setAddress(request.getAddress());
//            bank.setIsActive(request.getIsActive());
//            return bank;
//        }
//    }
//
//    public BankAdmin toBankAdmin(BankCreateRequest request) {
//        if (request == null) {
//            return null;
//        } else {
//            BankAdmin bankAdmin = new BankAdmin();
//            bankAdmin.setUsername(this.requestAdminUsername(request));
//            bankAdmin.setFullName(this.requestAdminFullName(request));
//            bankAdmin.setEmail(this.requestAdminEmail(request));
//            bankAdmin.setPassword(this.requestAdminPassword(request));
//            return bankAdmin;
//        }
//    }
//
//    public BankResponse toBankResponse(Bank bank) {
//        if (bank == null) {
//            return null;
//        } else {
//            BankResponse.AdminResponse admin = null;
//            String name = null;
//            String bankCode = null;
//            Boolean isActive = null;
//            String address = null;
//            admin = this.bankAdminToAdminResponse(bank.getBankAdmin());
//            name = bank.getName();
//            bankCode = bank.getBankCode();
//            isActive = bank.getIsActive();
//            address = bank.getAddress();
//            BankResponse bankResponse = new BankResponse(name, bankCode, isActive, address, admin);
//            return bankResponse;
//        }
//    }
//
//    private String requestAdminUsername(BankCreateRequest bankCreateRequest) {
//        BankCreateRequest.AdminRequest admin = bankCreateRequest.getAdmin();
//        return admin == null ? null : admin.getUsername();
//    }
//
//    private String requestAdminFullName(BankCreateRequest bankCreateRequest) {
//        BankCreateRequest.AdminRequest admin = bankCreateRequest.getAdmin();
//        return admin == null ? null : admin.getFullName();
//    }
//
//    private String requestAdminEmail(BankCreateRequest bankCreateRequest) {
//        BankCreateRequest.AdminRequest admin = bankCreateRequest.getAdmin();
//        return admin == null ? null : admin.getEmail();
//    }
//
//    private String requestAdminPassword(BankCreateRequest bankCreateRequest) {
//        BankCreateRequest.AdminRequest admin = bankCreateRequest.getAdmin();
//        return admin == null ? null : admin.getPassword();
//    }
//
//    private Long bankAdminAccessGroupId(BankAdmin bankAdmin) {
//        AccessGroup accessGroup = bankAdmin.getAccessGroup();
//        return accessGroup == null ? null : accessGroup.getId();
//    }
//
//    protected BankResponse.AdminResponse bankAdminToAdminResponse(BankAdmin bankAdmin) {
//        if (bankAdmin == null) {
//            return null;
//        } else {
//            String fullName = null;
//            String email = null;
//            String username = null;
//            Long accessGroupId = null;
//            fullName = bankAdmin.getFullName();
//            email = bankAdmin.getEmail();
//            username = bankAdmin.getUsername();
//            accessGroupId = this.bankAdminAccessGroupId(bankAdmin);
//            BankResponse.AdminResponse adminResponse = new BankResponse.AdminResponse(fullName, email, username, accessGroupId);
//            return adminResponse;
//        }
//    }
//}
