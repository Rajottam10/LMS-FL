package com.ebooks.accrualsaccountingservice.services.impl;

import com.ebooks.accrualsaccountingservice.entities.InterestInfo;
import com.ebooks.accrualsaccountingservice.entities.LateFeeInfo;
import com.ebooks.accrualsaccountingservice.repositories.LateFeeInfoRepository;
import com.ebooks.accrualsaccountingservice.services.LateFeeInfoService;
import org.springframework.stereotype.Service;


@Service
public class LateFeeInfoServiceImpl implements LateFeeInfoService {
    private final LateFeeInfoRepository lateFeeInfoRepository;

    public LateFeeInfoServiceImpl(LateFeeInfoRepository lateFeeInfoRepository){
        this.lateFeeInfoRepository = lateFeeInfoRepository;
    }

    @Override
    public void createLateFee(LateFeeInfo lateFeeInfo){
        lateFeeInfoRepository.save(lateFeeInfo);
    }

}
