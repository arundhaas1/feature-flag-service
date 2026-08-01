package com.flag.featureflagservice.service;

import com.flag.featureflagservice.model.Env;
import com.flag.featureflagservice.model.Environment;
import com.flag.featureflagservice.model.FeatureFlagState;
import com.flag.featureflagservice.repository.FeatureFlagRepository;
import com.flag.featureflagservice.repository.environmentRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class FeatureFlagService {
    FeatureFlagRepository featureFlagRepository;

    public FeatureFlagService(FeatureFlagRepository featureFlagRepository){
        this.featureFlagRepository = featureFlagRepository;
    }

    public FeatureFlagState getFlag(Long id, Long envId){
        Environment environment = environmentRepository.get(envId);
        return featureFlagRepository.getFlag(id, environment);
    }
}
