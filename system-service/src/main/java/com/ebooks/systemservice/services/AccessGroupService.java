package com.ebooks.systemservice.services;


import com.ebooks.commonservice.dtos.AccessGroupRequestDto;
import com.ebooks.commonservice.dtos.AccessGroupResponseDto;

import java.util.List;

public interface AccessGroupService {

    AccessGroupResponseDto createAccessGroup(AccessGroupRequestDto requestDTO);

    List<AccessGroupResponseDto> getAllAccessGroups();

    AccessGroupResponseDto getAccessGroupById(Long id);

    AccessGroupResponseDto updateAccessGroup(Long id, AccessGroupRequestDto requestDTO);

    void deleteAccessGroup(Long id);

    List<AccessGroupResponseDto> getAccessGroupsByType(String type);
}