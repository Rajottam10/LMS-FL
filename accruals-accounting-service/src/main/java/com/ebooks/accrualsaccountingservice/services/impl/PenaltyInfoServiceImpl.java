package com.ebooks.accrualsaccountingservice.services.impl;

import com.ebooks.accrualsaccountingservice.entities.PenaltyInfo;
import com.ebooks.accrualsaccountingservice.repositories.PenaltyInfoRepository;
import com.ebooks.accrualsaccountingservice.services.PenaltyInfoService;
import org.springframework.stereotype.Service;

@Service
public class PenaltyInfoServiceImpl implements PenaltyInfoService {
    private final PenaltyInfoRepository penaltyInfoRepository;

    public PenaltyInfoServiceImpl(PenaltyInfoRepository penaltyInfoRepository){
        this.penaltyInfoRepository = penaltyInfoRepository;
    }
    @Override
    public void createPenalty(PenaltyInfo penaltyInfo){
        penaltyInfoRepository.save(penaltyInfo);
    }
}
