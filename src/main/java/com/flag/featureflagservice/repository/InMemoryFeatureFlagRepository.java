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
        return featureFlag.get(id);
    }

    @Override
    public FeatureFlag addFlag(FeatureFlag flag) {
        //update next flow
        featureFlag.put(flag.getId(), getDummyFlag(flag.getId(), flag.getId(), flag.getFlagKey(), false));
        return flag;
    }

    @Override
    public FeatureFlagState updateFlag(FeatureFlagState flag) {
        featureFlag.put(flag.getId(), flag);
        return featureFlag.get(flag.getId());
    }

    @Override
    public FeatureFlagState[] getFlagList(Application application, Environment environment) {
        return featureFlag.values().toArray(new FeatureFlagState[featureFlag.size()]);
    }

    @Override
    public void deleteFlag(Long flagId) {
        featureFlag.remove(flagId);
    }

    private FeatureFlagState getDummyFlag(Long id, Long flagId, String flagKey, boolean isEnabled){
        Application app =  applicationRepository.findById(id).orElse(null);
        FeatureFlag flag = new FeatureFlag(flagId,flagKey, flagKey, app, Instant.now(), "Arun");
        Environment environment = environmentRepository.get(33L);
        return new FeatureFlagState(id,flag, environment, isEnabled, 1);
    }
}
