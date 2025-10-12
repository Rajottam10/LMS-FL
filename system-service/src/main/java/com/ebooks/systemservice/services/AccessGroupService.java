package com.ebooks.systemservice.services;

import com.ebooks.commonservice.dtos.AccessGroupRequestDto;
import com.ebooks.commonservice.dtos.AccessGroupResponseDto;

import java.util.List;

public interface AccessGroupService {
    AccessGroupResponseDto createAccessGroup(AccessGroupRequestDto accessGroupRequestDto);
    AccessGroupResponseDto getAccessGroupById(Long id);
    List<AccessGroupResponseDto> getAllAccessGroup();
    AccessGroupResponseDto updateAccessGroup(Long id, AccessGroupRequestDto accessGroupRequestDto);
    void deleteAccessGroup(Long id);
}
