package com.flag.featureflagservice.service;

import com.flag.featureflagservice.repository.FeatureFlagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FeatureFlagService {
    FeatureFlagRepository featureFlagRepository;

    public FeatureFlagService(FeatureFlagRepository featureFlagRepository){
        this.featureFlagRepository = featureFlagRepository;
    }

    public Map<String, Boolean> getFlagList(String app){
        return featureFlagRepository.getFlagList(app);
    }
}
