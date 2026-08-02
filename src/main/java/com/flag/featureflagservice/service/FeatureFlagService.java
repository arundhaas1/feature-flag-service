package com.flag.featureflagservice.service;

import com.flag.featureflagservice.controller.input.AddFeatureFlagRequest;
import com.flag.featureflagservice.model.*;
import com.flag.featureflagservice.repository.ApplicationRepository;
import com.flag.featureflagservice.repository.EnvironmentRepository;
import com.flag.featureflagservice.repository.FeatureFlagRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class FeatureFlagService {
    private final FeatureFlagRepository featureFlagRepository;
    private final EnvironmentRepository environmentRepository;
    private final ApplicationRepository applicationRepository;

    public FeatureFlagService(FeatureFlagRepository featureFlagRepository, EnvironmentRepository environmentRepository, ApplicationRepository applicationRepository){
        this.featureFlagRepository = featureFlagRepository;
        this.environmentRepository = environmentRepository;
        this.applicationRepository = applicationRepository;
    }

    public FeatureFlagState getFlag(String application, Long id, Long envId){
        Environment environment = environmentRepository.get(envId);
        return featureFlagRepository.getFlag(id, environment);
    }

    public FeatureFlagState updateFlag(FeatureFlagState flag){
        return featureFlagRepository.updateFlag(flag);
    }

    public FeatureFlag addFlag(String appName, AddFeatureFlagRequest flagRequest){
        Application application = applicationRepository.findByName(appName).orElseThrow();
        FeatureFlag flag = new FeatureFlag(null, flagRequest.getName(), flagRequest.getDescription(), application, Instant.now(), "Arun");
        return featureFlagRepository.addFlag(flag);
    }

    public void deleteFlag(FeatureFlag flag){

        featureFlagRepository.deleteFlag(flag.getId());
    }

    public FeatureFlagState[] getFlagList(Long appId, Long envId){
        Environment environment = environmentRepository.get(envId);
        Application application = applicationRepository.findById(appId).orElseThrow();
        return featureFlagRepository.getFlagList(application, environment);
    }
}
