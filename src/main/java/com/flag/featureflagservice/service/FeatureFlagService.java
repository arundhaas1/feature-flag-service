package com.flag.featureflagservice.service;

import com.flag.featureflagservice.controller.input.AddFeatureFlagRequest;
import com.flag.featureflagservice.controller.output.FeatureFlagStateResponse;
import com.flag.featureflagservice.model.*;
import com.flag.featureflagservice.repository.ApplicationRepository;
import com.flag.featureflagservice.repository.EnvironmentRepository;
import com.flag.featureflagservice.repository.FeatureFlagRepository;
import com.flag.featureflagservice.repository.FeatureFlagStateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class FeatureFlagService {
    private final FeatureFlagRepository featureFlagRepository;
    private final FeatureFlagStateRepository featureFlagStateRepository;
    private final EnvironmentRepository environmentRepository;
    private final ApplicationRepository applicationRepository;

    public FeatureFlagService(FeatureFlagRepository featureFlagRepository,
                              FeatureFlagStateRepository featureFlagStateRepository,
                              EnvironmentRepository environmentRepository,
                              ApplicationRepository applicationRepository) {
        this.featureFlagRepository = featureFlagRepository;
        this.featureFlagStateRepository = featureFlagStateRepository;
        this.environmentRepository = environmentRepository;
        this.applicationRepository = applicationRepository;
    }

    public FeatureFlagStateResponse getFlag(String appName, Long flagId, Long environmentId) {
        FeatureFlagState state = featureFlagStateRepository
                .findByFlagIdAndEnvironmentId(flagId, environmentId)
                .orElseThrow();
        return new FeatureFlagStateResponse(state);
    }

    public FeatureFlagState updateFlag(FeatureFlagState state) {
        return featureFlagStateRepository.save(state);
    }

    @Transactional
    public FeatureFlag addFlag(String appName, AddFeatureFlagRequest flagRequest) {
        Application application = applicationRepository.findByName(appName).orElseThrow();
        FeatureFlag flag = featureFlagRepository.save(
                new FeatureFlag(null, flagRequest.getName(), flagRequest.getDescription(),
                        application, Instant.now(), "Arun"));

        for (Long environmentId : flagRequest.getEnvironmentId()) {
            Environment environment = environmentRepository.findById(environmentId).orElseThrow();
            featureFlagStateRepository.save(new FeatureFlagState(null, flag, environment, false, 0));
        }
        return flag;
    }

    @Transactional
    public void deleteFlag(Long flagId) {
        featureFlagStateRepository.deleteByFlagId(flagId);
        featureFlagRepository.deleteById(flagId);
    }

    public List<FeatureFlagStateResponse> getFlagList(Long appId, Long environmentId) {
        return featureFlagStateRepository
                .findByFlagApplicationIdAndEnvironmentId(appId, environmentId)
                .stream()
                .map(FeatureFlagStateResponse::new)
                .toList();
    }
}
