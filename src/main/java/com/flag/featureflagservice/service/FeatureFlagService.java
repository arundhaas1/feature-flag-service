package com.flag.featureflagservice.service;

import com.flag.featureflagservice.cache.FlagCache;
import com.flag.featureflagservice.cache.FlagCacheKey;
import com.flag.featureflagservice.evaluation.FlagRules;
import com.flag.featureflagservice.controller.input.AddFeatureFlagRequest;
import com.flag.featureflagservice.controller.input.AddOverrideRequest;
import com.flag.featureflagservice.controller.input.UpdateFeatureFlagRequest;
import com.flag.featureflagservice.controller.output.FeatureFlagStateResponse;
import com.flag.featureflagservice.controller.output.FlagOverrideResponse;
import com.flag.featureflagservice.exception.ApplicationNotFoundException;
import com.flag.featureflagservice.exception.EnvironmentNotFoundException;
import com.flag.featureflagservice.exception.FeatureFlagNotFoundException;
import com.flag.featureflagservice.exception.OverrideNotFoundException;
import com.flag.featureflagservice.model.*;
import com.flag.featureflagservice.repository.ApplicationRepository;
import com.flag.featureflagservice.repository.EnvironmentRepository;
import com.flag.featureflagservice.repository.FeatureFlagRepository;
import com.flag.featureflagservice.repository.FeatureFlagStateRepository;
import com.flag.featureflagservice.repository.FlagOverrideRepository;
import com.flag.featureflagservice.security.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeatureFlagService {
    private final FeatureFlagRepository featureFlagRepository;
    private final FeatureFlagStateRepository featureFlagStateRepository;
    private final EnvironmentRepository environmentRepository;
    private final ApplicationRepository applicationRepository;
    private final CurrentUserProvider currentUserProvider;
    private final FlagCache flagCache;
    private final FlagOverrideRepository flagOverrideRepository;

    public FeatureFlagService(FeatureFlagRepository featureFlagRepository,
                              FeatureFlagStateRepository featureFlagStateRepository,
                              EnvironmentRepository environmentRepository,
                              ApplicationRepository applicationRepository,
                              CurrentUserProvider currentUserProvider,
                              FlagCache flagCache,
                              FlagOverrideRepository flagOverrideRepository) {
        this.featureFlagRepository = featureFlagRepository;
        this.featureFlagStateRepository = featureFlagStateRepository;
        this.environmentRepository = environmentRepository;
        this.applicationRepository = applicationRepository;
        this.currentUserProvider = currentUserProvider;
        this.flagCache = flagCache;
        this.flagOverrideRepository = flagOverrideRepository;
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
        FeatureFlagState saved = featureFlagStateRepository.save(state);
        flagCache.evict(keyFor(saved));
        return new FeatureFlagStateResponse(saved);
    }

    @Transactional
    public FeatureFlag addFlag(String appName, AddFeatureFlagRequest flagRequest) {
        Application application = applicationRepository.findByName(appName)
                .orElseThrow(() -> new ApplicationNotFoundException(appName));
        FeatureFlag flag = featureFlagRepository.save(
                new FeatureFlag(null, flagRequest.getName(), flagRequest.getDescription(),
                        application, Instant.now(), currentUserProvider.currentUsername()));

        for (Long environmentId : flagRequest.getEnvironmentId()) {
            Environment environment = environmentRepository.findById(environmentId)
                    .orElseThrow(() -> new EnvironmentNotFoundException(environmentId));
            featureFlagStateRepository.save(new FeatureFlagState(null, flag, environment, false, 0));
        }
        return flag;
    }

    @Transactional
    public void deleteFlag(Long flagId) {
        if (!featureFlagRepository.existsById(flagId)) {
            throw new FeatureFlagNotFoundException(flagId);
        }

        // Read the keys before the rows go: afterwards there is nothing left to derive them from.
        List<FlagCacheKey> staleKeys = featureFlagStateRepository.findByFlagId(flagId).stream()
                .map(FeatureFlagService::keyFor)
                .toList();

        flagOverrideRepository.deleteByFlagId(flagId);
        featureFlagStateRepository.deleteByFlagId(flagId);
        featureFlagRepository.deleteById(flagId);

        staleKeys.forEach(flagCache::evict);
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

    public boolean evaluate(String flagKey, String appName, String env, String organisationId) {
        FlagCacheKey key = new FlagCacheKey(flagKey, appName, env);

        Optional<FlagRules> cached = flagCache.lookup(key);
        if (cached.isPresent()) {
            return cached.get().resolve(organisationId);
        }

        Optional<FeatureFlagState> state =
                featureFlagStateRepository.findForEvaluation(flagKey, appName, env);
        if (state.isEmpty()) {
            if (!environmentRepository.existsByName(env)) {
                throw new EnvironmentNotFoundException(env);
            }
            // Deliberately not cached: a flag that does not exist yet may be created at any
            // moment, and caching the miss would hide it until the entry expired.
            return false;
        }

        FlagRules rules = new FlagRules(state.get().isEnabled(), organisationOverrides(flagKey, appName, env));
        flagCache.store(key, rules);
        return rules.resolve(organisationId);
    }

    private Map<String, Boolean> organisationOverrides(String flagKey, String appName, String env) {
        return flagOverrideRepository.findForEvaluation(flagKey, appName, env, OverrideScope.ORG).stream()
                .collect(Collectors.toMap(FlagOverride::getScopeValue, FlagOverride::isEnabled));
    }

    @Transactional
    public FlagOverrideResponse addOverride(Long flagId, AddOverrideRequest request) {
        FeatureFlag flag = featureFlagRepository.findById(flagId)
                .orElseThrow(() -> new FeatureFlagNotFoundException(flagId));
        Environment environment = environmentRepository.findById(request.getEnvironmentId())
                .orElseThrow(() -> new EnvironmentNotFoundException(request.getEnvironmentId()));

        FlagOverride saved = flagOverrideRepository.save(new FlagOverride(null, flag, environment,
                OverrideScope.ORG, request.getOrgId(), request.getEnabled(),
                Instant.now(), currentUserProvider.currentUsername()));

        flagCache.evict(new FlagCacheKey(flag.getFlagKey(), flag.getApplication().getName(),
                environment.getName()));
        return new FlagOverrideResponse(saved);
    }

    @Transactional
    public void deleteOverride(Long overrideId) {
        FlagOverride override = flagOverrideRepository.findById(overrideId)
                .orElseThrow(() -> new OverrideNotFoundException(overrideId));

        // Build the key while the associations are still loadable.
        FlagCacheKey key = new FlagCacheKey(override.getFlag().getFlagKey(),
                override.getFlag().getApplication().getName(), override.getEnvironment().getName());

        flagOverrideRepository.delete(override);
        flagCache.evict(key);
    }

    public List<FlagOverrideResponse> getOverrides(Long flagId, Long environmentId) {
        return flagOverrideRepository.findByFlagIdAndEnvironmentId(flagId, environmentId).stream()
                .map(FlagOverrideResponse::new)
                .toList();
    }

    private static FlagCacheKey keyFor(FeatureFlagState state) {
        return new FlagCacheKey(state.getFlag().getFlagKey(),
                state.getFlag().getApplication().getName(),
                state.getEnvironment().getName());
    }
}
