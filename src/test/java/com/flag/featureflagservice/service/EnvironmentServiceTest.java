package com.flag.featureflagservice.service;

import com.flag.featureflagservice.controller.output.EnvironmentResponse;
import com.flag.featureflagservice.model.Environment;
import com.flag.featureflagservice.repository.EnvironmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static com.flag.featureflagservice.TestConstants.ENVIRONMENT_DESCRIPTION;
import static com.flag.featureflagservice.TestConstants.ENVIRONMENT_ID;
import static com.flag.featureflagservice.TestConstants.ENVIRONMENT_NAME;
import static com.flag.featureflagservice.TestConstants.SYSTEM_USER;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("EnvironmentService")
class EnvironmentServiceTest {

    @Mock
    private EnvironmentRepository environmentRepository;

    @InjectMocks
    private EnvironmentService environmentService;

    @Test
    @DisplayName("Given stored environments, when listing, then each is mapped to a response")
    void givenStoredEnvironments_whenGetAll_thenMapsToResponses() {
        when(environmentRepository.findAll()).thenReturn(List.of(environment()));

        List<EnvironmentResponse> responses = environmentService.getAll();

        assertAll(
                () -> assertEquals(1, responses.size()),
                () -> assertEquals(ENVIRONMENT_ID, responses.get(0).id()),
                () -> assertEquals(ENVIRONMENT_NAME, responses.get(0).name()),
                () -> assertEquals(ENVIRONMENT_DESCRIPTION, responses.get(0).description())
        );
    }

    @Test
    @DisplayName("Given no environments, when listing, then an empty list is returned")
    void givenNoEnvironments_whenGetAll_thenReturnsEmptyList() {
        when(environmentRepository.findAll()).thenReturn(List.of());

        assertTrue(environmentService.getAll().isEmpty());
    }

    private Environment environment() {
        return new Environment(ENVIRONMENT_ID, ENVIRONMENT_NAME, ENVIRONMENT_DESCRIPTION,
                Instant.now(), SYSTEM_USER);
    }
}
