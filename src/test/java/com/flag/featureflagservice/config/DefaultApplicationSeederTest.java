package com.flag.featureflagservice.config;

import com.flag.featureflagservice.model.Application;
import com.flag.featureflagservice.repository.ApplicationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static com.flag.featureflagservice.TestConstants.APP_DESCRIPTION;
import static com.flag.featureflagservice.TestConstants.SYSTEM_USER;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultApplicationSeeder")
class DefaultApplicationSeederTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private DefaultApplicationSeeder defaultApplicationSeeder;

    @Test
    @DisplayName("Given no default application, when the seeder runs, then it creates one owned by system")
    void givenNoDefaultApplication_whenRun_thenCreatesOneOwnedBySystem() {
        when(applicationRepository.findByName(DefaultApplicationSeeder.DEFAULT_APPLICATION_NAME))
                .thenReturn(Optional.empty());
        ArgumentCaptor<Application> captor = ArgumentCaptor.forClass(Application.class);

        defaultApplicationSeeder.run();

        verify(applicationRepository).save(captor.capture());
        assertAll(
                () -> assertEquals(DefaultApplicationSeeder.DEFAULT_APPLICATION_NAME,
                        captor.getValue().getName()),
                () -> assertEquals(SYSTEM_USER, captor.getValue().getCreatedBy())
        );
    }

    @Test
    @DisplayName("Given the default application exists, when the seeder runs, then nothing is written")
    void givenDefaultApplicationExists_whenRun_thenNothingIsWritten() {
        when(applicationRepository.findByName(DefaultApplicationSeeder.DEFAULT_APPLICATION_NAME))
                .thenReturn(Optional.of(existing()));

        defaultApplicationSeeder.run();

        verify(applicationRepository, never()).save(any(Application.class));
    }

    private Application existing() {
        return new Application(1L, DefaultApplicationSeeder.DEFAULT_APPLICATION_NAME,
                APP_DESCRIPTION, SYSTEM_USER, Instant.now());
    }
}
