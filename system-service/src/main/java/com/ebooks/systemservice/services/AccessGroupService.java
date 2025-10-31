package com.ebooks.systemservice.services;


import com.ebooks.commonservice.dtos.AccessGroupRequest;
import com.ebooks.commonservice.dtos.AccessGroupResponse;

import java.util.List;

public interface AccessGroupService {
    List<AccessGroupResponse> getAllAccessGroup();

    AccessGroupResponse createAccessGroup(AccessGroupRequest request);

    AccessGroupResponse findAccessGroupById(Long accessGroupId);

    void deleteAccessGroup(Long accessGroupId);

    AccessGroupResponse updateAccessGroup(Long accessGroupId, AccessGroupRequest request);
}
