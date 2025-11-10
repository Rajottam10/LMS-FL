//package com.ebooks.systemservice.mapper;
//
//import com.ebooks.commonservice.entities.Bank;
//import com.ebooks.commonservice.entities.BankAdmin;
//import com.ebooks.systemservice.dtos.BankCreateRequest;
//import com.ebooks.systemservice.dtos.BankResponse;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.Mappings;
//
//@Mapper(
//        componentModel = "spring"
//)
//public interface BankMapper {
//    @Mappings({@Mapping(
//            target = "status",
//            ignore = true
//    ), @Mapping(
//            target = "createdAt",
//            ignore = true
//    )})
//    Bank toBank(BankCreateRequest request);
//
//    @Mappings({@Mapping(
//            source = "admin.username",
//            target = "username"
//    ), @Mapping(
//            source = "admin.fullName",
//            target = "fullName"
//    ), @Mapping(
//            source = "admin.email",
//            target = "email"
//    ), @Mapping(
//            source = "admin.password",
//            target = "password"
//    ), @Mapping(
//            target = "status",
//            ignore = true
//    ), @Mapping(
//            target = "bank",
//            ignore = true
//    ), @Mapping(
//            target = "accessGroup",
//            ignore = true
//    )})
//    BankAdmin toBankAdmin(BankCreateRequest request);
//
//    @Mappings({@Mapping(
//            source = "bank.bankAdmin.fullName",
//            target = "admin.fullName"
//    ), @Mapping(
//            source = "bank.bankAdmin.email",
//            target = "admin.email"
//    ), @Mapping(
//            source = "bank.bankAdmin.username",
//            target = "admin.username"
//    ), @Mapping(
//            source = "bank.bankAdmin.accessGroup.id",
//            target = "admin.accessGroupId"
//    )})
//    BankResponse toBankResponse(Bank bank);
//}
