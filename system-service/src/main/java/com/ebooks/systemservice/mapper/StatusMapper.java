package com.ebooks.systemservice.mapper;

import com.ebooks.commonservice.dtos.StatusDto;
import com.ebooks.systemservice.entities.Status;
import org.springframework.stereotype.Component;

@Component
public class StatusMapper {
    public StatusDto statusToDto(Status status){
        StatusDto statusDto = new StatusDto();
        statusDto.setId(status.getId());
        statusDto.setName(status.getName());
        return statusDto;
    }

    public Status dtoToStatus(StatusDto statusDto){
        Status status = new Status();
        status.setId(statusDto.getId());
        status.setName(statusDto.getName());
        status.setIcon(null);
        status.setDescription(null);
        return status;
    }
}
