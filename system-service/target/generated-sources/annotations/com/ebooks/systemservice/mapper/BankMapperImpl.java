package com.ebooks.systemservice.mapper;

import com.ebooks.commonservice.entities.AccessGroup;
import com.ebooks.commonservice.entities.Bank;
import com.ebooks.commonservice.entities.BankAdmin;
import com.ebooks.systemservice.dtos.BankCreateRequest;
import com.ebooks.systemservice.dtos.BankResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-26T12:54:48+0545",
    comments = "version: 1.6.3, compiler: javac, environment: Java 24.0.1 (Oracle Corporation)"
)
@Component
public class BankMapperImpl implements BankMapper {

    @Override
    public Bank toBank(BankCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        Bank bank = new Bank();

        bank.setBankCode( request.getBankCode() );
        bank.setName( request.getName() );
        bank.setAddress( request.getAddress() );
        bank.setIsActive( request.getIsActive() );

        return bank;
    }

    @Override
    public BankAdmin toBankAdmin(BankCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        BankAdmin bankAdmin = new BankAdmin();

        bankAdmin.setUsername( requestAdminUsername( request ) );
        bankAdmin.setFullName( requestAdminFullName( request ) );
        bankAdmin.setEmail( requestAdminEmail( request ) );
        bankAdmin.setPassword( requestAdminPassword( request ) );

        return bankAdmin;
    }

    @Override
    public BankResponse toBankResponse(Bank bank) {
        if ( bank == null ) {
            return null;
        }

        BankResponse.AdminResponse admin = null;
        String name = null;
        String bankCode = null;
        Boolean isActive = null;
        String address = null;

        admin = bankAdminToAdminResponse( bank.getBankAdmin() );
        name = bank.getName();
        bankCode = bank.getBankCode();
        isActive = bank.getIsActive();
        address = bank.getAddress();

        BankResponse bankResponse = new BankResponse( name, bankCode, isActive, address, admin );

        return bankResponse;
    }

    private String requestAdminUsername(BankCreateRequest bankCreateRequest) {
        BankCreateRequest.AdminRequest admin = bankCreateRequest.getAdmin();
        if ( admin == null ) {
            return null;
        }
        return admin.getUsername();
    }

    private String requestAdminFullName(BankCreateRequest bankCreateRequest) {
        BankCreateRequest.AdminRequest admin = bankCreateRequest.getAdmin();
        if ( admin == null ) {
            return null;
        }
        return admin.getFullName();
    }

    private String requestAdminEmail(BankCreateRequest bankCreateRequest) {
        BankCreateRequest.AdminRequest admin = bankCreateRequest.getAdmin();
        if ( admin == null ) {
            return null;
        }
        return admin.getEmail();
    }

    private String requestAdminPassword(BankCreateRequest bankCreateRequest) {
        BankCreateRequest.AdminRequest admin = bankCreateRequest.getAdmin();
        if ( admin == null ) {
            return null;
        }
        return admin.getPassword();
    }

    private Long bankAdminAccessGroupId(BankAdmin bankAdmin) {
        AccessGroup accessGroup = bankAdmin.getAccessGroup();
        if ( accessGroup == null ) {
            return null;
        }
        return accessGroup.getId();
    }

    protected BankResponse.AdminResponse bankAdminToAdminResponse(BankAdmin bankAdmin) {
        if ( bankAdmin == null ) {
            return null;
        }

        String fullName = null;
        String email = null;
        String username = null;
        Long accessGroupId = null;

        fullName = bankAdmin.getFullName();
        email = bankAdmin.getEmail();
        username = bankAdmin.getUsername();
        accessGroupId = bankAdminAccessGroupId( bankAdmin );

        BankResponse.AdminResponse adminResponse = new BankResponse.AdminResponse( fullName, email, username, accessGroupId );

        return adminResponse;
    }
}
