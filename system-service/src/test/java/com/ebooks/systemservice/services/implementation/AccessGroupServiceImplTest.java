package com.ebooks.systemservice.services.implementation;

import com.ebooks.commonservice.mapper.AccessGroupMapper;
import com.ebooks.commonservice.repositories.AccessGroupRepository;
import com.ebooks.commonservice.repositories.AccessGroupRoleMapRepository;
import com.ebooks.commonservice.repositories.BankAdminRepository;
import com.ebooks.commonservice.repositories.RoleRepository;
import com.ebooks.commonservice.repositories.StatusRepository;
import com.ebooks.commonservice.services.AccessGroupRoleMapService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class) //indicated that this test class will use the mockito library
@DisplayName("AccessGroupServiceImpl Unit test")
class AccessGroupServiceImplTest {

    @Mock
    private  AccessGroupRepository accessGroupRepository; //the dependency that is in the original class aboot to be tested
    @Mock
    private  RoleRepository roleRepository;
    @Mock
    private  AccessGroupRoleMapRepository accessGroupRoleMapRepository;
    @Mock
    private  AccessGroupMapper accessGroupMapper;
    @Mock
    private  StatusRepository statusRepository;
    @Mock
    private  AccessGroupRoleMapService accessGroupRoleMapService; 
    @Mock
    private  BankAdminRepository bankAdminRepository;

    @InjectMocks
    private AccessGroupServiceImpl accessGroupService; //the class that we want to test

}