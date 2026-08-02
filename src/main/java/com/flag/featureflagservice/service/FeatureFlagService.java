package com.flag.featureflagservice.service;

import com.flag.featureflagservice.controller.input.AddFeatureFlagRequest;
import com.flag.featureflagservice.controller.input.UpdateFeatureFlagRequest;
import com.flag.featureflagservice.controller.output.FeatureFlagStateResponse;
import com.flag.featureflagservice.exception.ApplicationNotFoundException;
import com.flag.featureflagservice.exception.EnvironmentNotFoundException;
import com.flag.featureflagservice.exception.FeatureFlagNotFoundException;
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
                .orElseThrow(() -> new FeatureFlagNotFoundException(flagId));
        return new FeatureFlagStateResponse(state);
    }

    @Transactional
    public FeatureFlagStateResponse updateFlag(String appName, Long flagId, UpdateFeatureFlagRequest request) {
        FeatureFlagState state = featureFlagStateRepository
                .findByFlagIdAndEnvironmentId(flagId, request.getEnvironmentId())
                .orElseThrow(() -> new FeatureFlagNotFoundException(flagId));
        state.setEnabled(request.getEnabled());
        return new FeatureFlagStateResponse(featureFlagStateRepository.save(state));
    }

    @Transactional
    public FeatureFlag addFlag(String appName, AddFeatureFlagRequest flagRequest) {
        Application application = applicationRepository.findByName(appName)
                .orElseThrow(() -> new ApplicationNotFoundException(appName));
        FeatureFlag flag = featureFlagRepository.save(
                new FeatureFlag(null, flagRequest.getName(), flagRequest.getDescription(),
                        application, Instant.now(), "Arun"));

        for (Long environmentId : flagRequest.getEnvironmentId()) {
            Environment environment = environmentRepository.findById(environmentId)
                    .orElseThrow(() -> new EnvironmentNotFoundException(environmentId));
            featureFlagStateRepository.save(new FeatureFlagState(null, flag, environment, false, 0));
        }
        return flag;
    }

    @Transactional
    public void deleteFlag(Long flagId) {
        featureFlagStateRepository.deleteByFlagId(flagId);
        featureFlagRepository.deleteById(flagId);
    }

    public List<FeatureFlagStateResponse> getFlagList(String appName, Long environmentId) {
        Application application = applicationRepository.findByName(appName)
                .orElseThrow(() -> new ApplicationNotFoundException(appName));
        return featureFlagStateRepository
                .findByFlagApplicationIdAndEnvironmentId(application.getId(), environmentId)
                .stream()
                .map(FeatureFlagStateResponse::new)
                .toList();
    }

    public boolean evaluate(String flagKey, String appName, String env, Long userId) {
        // userId is reserved for per-user targeting rules (future); global toggle for now.
        return featureFlagStateRepository
                .findForEvaluation(flagKey, appName, env)
                .map(FeatureFlagState::isEnabled)
                .orElseGet(() -> {
                    if (!environmentRepository.existsByName(env)) {
                        throw new EnvironmentNotFoundException(env);
                    }
                    return false;
                });
    }
}
