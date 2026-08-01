package com.flag.featureflagservice.service;

import com.flag.featureflagservice.model.*;
import com.flag.featureflagservice.repository.EnvironmentRepository;
import com.flag.featureflagservice.repository.FeatureFlagRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class FeatureFlagService {
    private FeatureFlagRepository featureFlagRepository;
    private EnvironmentRepository environmentRepository;

    public FeatureFlagService(FeatureFlagRepository featureFlagRepository, EnvironmentRepository environmentRepository){
        this.featureFlagRepository = featureFlagRepository;
        this.environmentRepository = environmentRepository;
    }

    public FeatureFlagState getFlag(Long id, Long envId){
        Environment environment = environmentRepository.get(envId);
        return featureFlagRepository.getFlag(id, environment);
    }

    public FeatureFlagState updateFlag(FeatureFlagState flag){
        return featureFlagRepository.updateFlag(flag);
    }

    public FeatureFlag addFlag(FeatureFlag flag){
        return featureFlagRepository.addFlag(flag);
    }

    public void deleteFlag(FeatureFlag flag){
        featureFlagRepository.deleteFlag(flag.getId());
    }

    public FeatureFlagState[] getFlagList(){
        return featureFlagRepository.getFlagList(null, null);
    }
}
