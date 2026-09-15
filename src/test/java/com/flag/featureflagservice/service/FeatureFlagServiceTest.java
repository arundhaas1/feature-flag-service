package com.flag.featureflagservice.service;

import com.flag.featureflagservice.cache.FlagCache;
import com.flag.featureflagservice.cache.FlagCacheKey;
import com.flag.featureflagservice.controller.input.AddFeatureFlagRequest;
import com.flag.featureflagservice.controller.input.UpdateFeatureFlagRequest;
import com.flag.featureflagservice.exception.EnvironmentNotFoundException;
import com.flag.featureflagservice.model.Application;
import com.flag.featureflagservice.model.Environment;
import com.flag.featureflagservice.model.FeatureFlag;
import com.flag.featureflagservice.model.FeatureFlagState;
import com.flag.featureflagservice.repository.ApplicationRepository;
import com.flag.featureflagservice.repository.EnvironmentRepository;
import com.flag.featureflagservice.repository.FeatureFlagRepository;
import com.flag.featureflagservice.repository.FeatureFlagStateRepository;
import com.flag.featureflagservice.security.CurrentUserProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.flag.featureflagservice.TestConstants.APP_DESCRIPTION;
import static com.flag.featureflagservice.TestConstants.DEFAULT_APP;
import static com.flag.featureflagservice.TestConstants.ENVIRONMENT_DESCRIPTION;
import static com.flag.featureflagservice.TestConstants.ENVIRONMENT_ID;
import static com.flag.featureflagservice.TestConstants.ENVIRONMENT_NAME;
import static com.flag.featureflagservice.TestConstants.FLAG_DESCRIPTION;
import static com.flag.featureflagservice.TestConstants.FLAG_KEY;
import static com.flag.featureflagservice.TestConstants.OTHER_ENVIRONMENT_NAME;
import static com.flag.featureflagservice.TestConstants.SYSTEM_USER;
import static com.flag.featureflagservice.TestConstants.USERNAME;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("FeatureFlagService")
class FeatureFlagServiceTest {

    @Mock
    private FeatureFlagRepository featureFlagRepository;

    @Mock
    private FeatureFlagStateRepository featureFlagStateRepository;

    @Mock
    private EnvironmentRepository environmentRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private FlagCache flagCache;

    @InjectMocks
    private FeatureFlagService featureFlagService;

    @Test
    @DisplayName("Given a logged-in caller, when adding a flag, then they are recorded as the author")
    void givenLoggedInCaller_whenAddFlag_thenRecordsThemAsAuthor() {
        when(applicationRepository.findByName(DEFAULT_APP)).thenReturn(Optional.of(application()));
        when(currentUserProvider.currentUsername()).thenReturn(USERNAME);
        when(environmentRepository.findById(ENVIRONMENT_ID)).thenReturn(Optional.of(environment()));
        when(featureFlagRepository.save(any(FeatureFlag.class))).thenAnswer(call -> call.getArgument(0));

        FeatureFlag flag = featureFlagService.addFlag(DEFAULT_APP, addRequest());

        assertAll(
                () -> assertEquals(USERNAME, flag.getCreatedBy()),
                () -> assertEquals(FLAG_KEY, flag.getFlagKey())
        );
    }

    @Test
    @DisplayName("Given an unknown environment, when adding a flag, then it is rejected")
    void givenUnknownEnvironment_whenAddFlag_thenThrowsEnvironmentNotFound() {
        when(applicationRepository.findByName(DEFAULT_APP)).thenReturn(Optional.of(application()));
        when(currentUserProvider.currentUsername()).thenReturn(USERNAME);
        when(environmentRepository.findById(ENVIRONMENT_ID)).thenReturn(Optional.empty());
        when(featureFlagRepository.save(any(FeatureFlag.class))).thenAnswer(call -> call.getArgument(0));

        assertThrows(EnvironmentNotFoundException.class,
                () -> featureFlagService.addFlag(DEFAULT_APP, addRequest()));
    }

    @Test
    @DisplayName("Given a flag with a description, when listing, then the description is returned")
    void givenFlagWithDescription_whenGetFlagList_thenDescriptionIsReturned() {
        when(applicationRepository.findByName(DEFAULT_APP)).thenReturn(Optional.of(application()));
        when(featureFlagStateRepository.findByFlagApplicationIdAndEnvironmentId(1L, ENVIRONMENT_ID))
                .thenReturn(List.of(state(true)));

        var responses = featureFlagService.getFlagList(DEFAULT_APP, ENVIRONMENT_ID);

        assertEquals(FLAG_DESCRIPTION, responses.get(0).getDescription());
    }

    @Test
    @DisplayName("Given an enabled flag, when evaluating, then true is returned")
    void givenEnabledFlag_whenEvaluate_thenReturnsTrue() {
        when(featureFlagStateRepository.findForEvaluation(FLAG_KEY, DEFAULT_APP, ENVIRONMENT_NAME))
                .thenReturn(Optional.of(state(true)));

        assertTrue(featureFlagService.evaluate(FLAG_KEY, DEFAULT_APP, ENVIRONMENT_NAME));
    }

    @Test
    @DisplayName("Given an unknown flag in a known environment, when evaluating, then false is returned")
    void givenUnknownFlagInKnownEnvironment_whenEvaluate_thenReturnsFalse() {
        when(featureFlagStateRepository.findForEvaluation(FLAG_KEY, DEFAULT_APP, ENVIRONMENT_NAME))
                .thenReturn(Optional.empty());
        when(environmentRepository.existsByName(ENVIRONMENT_NAME)).thenReturn(true);

        assertFalse(featureFlagService.evaluate(FLAG_KEY, DEFAULT_APP, ENVIRONMENT_NAME));
    }

    @Test
    @DisplayName("Given an unknown environment, when evaluating, then it is rejected")
    void givenUnknownEnvironment_whenEvaluate_thenThrowsEnvironmentNotFound() {
        when(featureFlagStateRepository.findForEvaluation(FLAG_KEY, DEFAULT_APP, ENVIRONMENT_NAME))
                .thenReturn(Optional.empty());
        when(environmentRepository.existsByName(ENVIRONMENT_NAME)).thenReturn(false);

        assertThrows(EnvironmentNotFoundException.class,
                () -> featureFlagService.evaluate(FLAG_KEY, DEFAULT_APP, ENVIRONMENT_NAME));
    }

    @Test
    @DisplayName("Given a cached answer, when evaluating, then the database is never queried")
    void givenCachedAnswer_whenEvaluate_thenDatabaseIsNeverQueried() {
        when(flagCache.lookup(cacheKey())).thenReturn(Optional.of(true));

        boolean enabled = featureFlagService.evaluate(FLAG_KEY, DEFAULT_APP, ENVIRONMENT_NAME);

        assertTrue(enabled);
        verify(featureFlagStateRepository, never()).findForEvaluation(FLAG_KEY, DEFAULT_APP, ENVIRONMENT_NAME);
    }

    @Test
    @DisplayName("Given a cache miss that finds a flag, when evaluating, then the answer is cached")
    void givenCacheMissThatFindsFlag_whenEvaluate_thenAnswerIsCached() {
        when(flagCache.lookup(cacheKey())).thenReturn(Optional.empty());
        when(featureFlagStateRepository.findForEvaluation(FLAG_KEY, DEFAULT_APP, ENVIRONMENT_NAME))
                .thenReturn(Optional.of(state(true)));

        featureFlagService.evaluate(FLAG_KEY, DEFAULT_APP, ENVIRONMENT_NAME);

        verify(flagCache).store(cacheKey(), true);
    }

    @Test
    @DisplayName("Given a flag that does not exist, when evaluating, then the miss is not cached")
    void givenFlagThatDoesNotExist_whenEvaluate_thenMissIsNotCached() {
        when(flagCache.lookup(cacheKey())).thenReturn(Optional.empty());
        when(featureFlagStateRepository.findForEvaluation(FLAG_KEY, DEFAULT_APP, ENVIRONMENT_NAME))
                .thenReturn(Optional.empty());
        when(environmentRepository.existsByName(ENVIRONMENT_NAME)).thenReturn(true);

        featureFlagService.evaluate(FLAG_KEY, DEFAULT_APP, ENVIRONMENT_NAME);

        // Caching the miss would hide the flag until the entry expired, once someone created it.
        verify(flagCache, never()).store(cacheKey(), false);
    }

    @Test
    @DisplayName("Given a toggle, when updating a flag, then only that environment's entry is evicted")
    void givenToggle_whenUpdateFlag_thenOnlyThatEnvironmentEntryIsEvicted() {
        FeatureFlagState stored = state(false);
        when(featureFlagStateRepository.findByFlagIdAndEnvironmentId(1L, ENVIRONMENT_ID))
                .thenReturn(Optional.of(stored));
        when(featureFlagStateRepository.save(stored)).thenReturn(stored);

        featureFlagService.updateFlag(DEFAULT_APP, 1L, updateRequest());

        verify(flagCache).evict(cacheKey());
    }

    @Test
    @DisplayName("Given a flag in two environments, when deleting it, then only its own keys are evicted")
    void givenFlagInTwoEnvironments_whenDeleteFlag_thenOnlyItsOwnKeysAreEvicted() {
        when(featureFlagStateRepository.findByFlagId(1L))
                .thenReturn(List.of(stateIn(ENVIRONMENT_NAME, true), stateIn(OTHER_ENVIRONMENT_NAME, false)));

        featureFlagService.deleteFlag(1L);

        assertAll(
                () -> verify(flagCache).evict(cacheKey()),
                () -> verify(flagCache).evict(new FlagCacheKey(FLAG_KEY, DEFAULT_APP, OTHER_ENVIRONMENT_NAME)),
                () -> verify(flagCache, never()).evictAll()
        );
    }

    @Test
    @DisplayName("Given a flag with no state rows, when deleting it, then nothing is evicted")
    void givenFlagWithNoStateRows_whenDeleteFlag_thenNothingIsEvicted() {
        when(featureFlagStateRepository.findByFlagId(1L)).thenReturn(List.of());

        featureFlagService.deleteFlag(1L);

        assertAll(
                () -> verify(flagCache, never()).evict(cacheKey()),
                () -> verify(flagCache, never()).evictAll()
        );
    }

    private FlagCacheKey cacheKey() {
        return new FlagCacheKey(FLAG_KEY, DEFAULT_APP, ENVIRONMENT_NAME);
    }

    private UpdateFeatureFlagRequest updateRequest() {
        UpdateFeatureFlagRequest request = new UpdateFeatureFlagRequest();
        request.setEnabled(true);
        request.setEnvironmentId(ENVIRONMENT_ID);
        return request;
    }

    private AddFeatureFlagRequest addRequest() {
        AddFeatureFlagRequest request = new AddFeatureFlagRequest();
        request.setName(FLAG_KEY);
        request.setDescription(FLAG_DESCRIPTION);
        request.setEnvironmentId(new Long[]{ENVIRONMENT_ID});
        return request;
    }

    private Application application() {
        return new Application(1L, DEFAULT_APP, APP_DESCRIPTION, SYSTEM_USER, Instant.now());
    }

    private Environment environment() {
        return new Environment(ENVIRONMENT_ID, ENVIRONMENT_NAME, ENVIRONMENT_DESCRIPTION,
                Instant.now(), SYSTEM_USER);
    }

    private FeatureFlagState stateIn(String environmentName, boolean enabled) {
        FeatureFlag flag = new FeatureFlag(1L, FLAG_KEY, FLAG_DESCRIPTION, application(),
                Instant.now(), USERNAME);
        Environment environment = new Environment(ENVIRONMENT_ID, environmentName,
                ENVIRONMENT_DESCRIPTION, Instant.now(), SYSTEM_USER);
        return new FeatureFlagState(1L, flag, environment, enabled, 0);
    }

    private FeatureFlagState state(boolean enabled) {
        FeatureFlag flag = new FeatureFlag(1L, FLAG_KEY, FLAG_DESCRIPTION, application(),
                Instant.now(), USERNAME);
        return new FeatureFlagState(1L, flag, environment(), enabled, 0);
    }
}
