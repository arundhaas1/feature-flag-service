package com.flag.featureflagservice.service;

import com.flag.featureflagservice.repository.FeatureFlagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class FeatureFlagService {
    FeatureFlagRepository featureFlagRepository;
    Integer value;

    @Autowired
    public FeatureFlagService(FeatureFlagRepository featureFlagRepository){
        this.featureFlagRepository = featureFlagRepository;
    }

    public FeatureFlagService(FeatureFlagRepository featureFlagRepository, Integer v){
        this.featureFlagRepository = featureFlagRepository;
        this.value = 12;
    }

    public Map<String, Boolean> getFlagList(String app){
        return featureFlagRepository.getFlagList(app);
    }
}
