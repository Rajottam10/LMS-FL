package com.ebooks.accrualsaccountingservice.services.impl;

import com.ebooks.accrualsaccountingservice.entities.OverdueInfo;
import com.ebooks.accrualsaccountingservice.repositories.OverdueInfoRepository;
import com.ebooks.accrualsaccountingservice.services.OverdueInfoService;
import org.springframework.stereotype.Service;

@Service
public class OverdueInfoInfoServiceImpl implements OverdueInfoService {
    private final OverdueInfoRepository overdueInfoRepository;

    public OverdueInfoInfoServiceImpl(OverdueInfoRepository overdueInfoRepository){
        this.overdueInfoRepository = overdueInfoRepository;
    }

    @Override
    public void createOverdue(OverdueInfo overdueInfo) {
        overdueInfoRepository.save(overdueInfo);
    }

}
