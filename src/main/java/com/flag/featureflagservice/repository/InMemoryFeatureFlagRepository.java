package com.flag.featureflagservice.repository;

import com.flag.featureflagservice.model.*;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryFeatureFlagRepository implements FeatureFlagRepository {
    private final Map<Long, FeatureFlagState> featureFlag = new HashMap<>();
    private ApplicationRepository applicationRepository;
    private EnvironmentRepository environmentRepository;

    public InMemoryFeatureFlagRepository(ApplicationRepository applicationRepository, EnvironmentRepository environmentRepository){
        this.applicationRepository = applicationRepository;
        this.environmentRepository = environmentRepository;

        featureFlag.put(1L, getDummyFlag(1L, 11L, "tickets", true));
        featureFlag.put(2L, getDummyFlag(2L, 12L, "localistion", true));
        featureFlag.put(3L, getDummyFlag(3L, 13L, "workstation", false));
        featureFlag.put(4L, getDummyFlag(4L, 14L, "products", false));
        featureFlag.put(5L, getDummyFlag(5L ,15L, "contacts", true));
        featureFlag.put(6L, getDummyFlag(6L, 16L, "comments", false));
    }

    @Override
    public FeatureFlagState getFlag(Long id, Environment environment) {
        return null;
    }

    @Override
    public FeatureFlag addFlag(FeatureFlag flag) {
        return null;
    }

    @Override
    public FeatureFlagState updateFlag(FeatureFlagState flag) {
        return null;
    }

    @Override
    public FeatureFlagState[] getFlagList(FeatureFlagState flag) {
        return new FeatureFlagState[0];
    }

    @Override
    public void deleteFlag(FeatureFlagState flag) {

    }

    private FeatureFlagState getDummyFlag(Long id, Long flagId, String flagKey, boolean isEnabled){
        Application app =  applicationRepository.getApplication(id);
        FeatureFlag flag = new FeatureFlag(flagId,flagKey, flagKey, app, Instant.now(), "Arun");
        Environment environment = environmentRepository.getEnvironment(33L);
        return new FeatureFlagState(id,flag, environment, isEnabled, 1);
    }
}
