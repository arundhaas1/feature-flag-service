package com.flag.featureflagservice.service;

import com.flag.featureflagservice.controller.input.AddFeatureFlagRequest;
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
import java.util.Optional;

import static com.flag.featureflagservice.TestConstants.APP_DESCRIPTION;
import static com.flag.featureflagservice.TestConstants.DEFAULT_APP;
import static com.flag.featureflagservice.TestConstants.ENVIRONMENT_DESCRIPTION;
import static com.flag.featureflagservice.TestConstants.ENVIRONMENT_ID;
import static com.flag.featureflagservice.TestConstants.ENVIRONMENT_NAME;
import static com.flag.featureflagservice.TestConstants.FLAG_DESCRIPTION;
import static com.flag.featureflagservice.TestConstants.FLAG_KEY;
import static com.flag.featureflagservice.TestConstants.SYSTEM_USER;
import static com.flag.featureflagservice.TestConstants.USERNAME;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

    private FeatureFlagState state(boolean enabled) {
        FeatureFlag flag = new FeatureFlag(1L, FLAG_KEY, FLAG_DESCRIPTION, application(),
                Instant.now(), USERNAME);
        return new FeatureFlagState(1L, flag, environment(), enabled, 0);
    }
}
