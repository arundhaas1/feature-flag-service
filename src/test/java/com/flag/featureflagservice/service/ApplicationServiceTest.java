package com.flag.featureflagservice.service;

import com.flag.featureflagservice.controller.input.AddApplicationRequest;
import com.flag.featureflagservice.model.Application;
import com.flag.featureflagservice.repository.ApplicationRepository;
import com.flag.featureflagservice.security.CurrentUserProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.flag.featureflagservice.TestConstants.APP_DESCRIPTION;
import static com.flag.featureflagservice.TestConstants.DEFAULT_APP;
import static com.flag.featureflagservice.TestConstants.USERNAME;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApplicationService")
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private ApplicationService applicationService;

    @Test
    @DisplayName("Given a logged-in caller, when adding an application, then they are recorded as the author")
    void givenLoggedInCaller_whenAddApplication_thenRecordsThemAsAuthor() {
        when(currentUserProvider.currentUsername()).thenReturn(USERNAME);
        when(applicationRepository.save(any(Application.class))).thenAnswer(call -> call.getArgument(0));

        Application saved = applicationService.addApplication(request());

        assertAll(
                () -> assertEquals(USERNAME, saved.getCreatedBy()),
                () -> assertEquals(DEFAULT_APP, saved.getName()),
                () -> assertEquals(APP_DESCRIPTION, saved.getDescription()),
                () -> assertNotNull(saved.getCreatedAt())
        );
    }

    @Test
    @DisplayName("Given a client-supplied id, when adding an application, then the id is ignored")
    void givenClientSuppliedId_whenAddApplication_thenIdIsIgnored() {
        when(currentUserProvider.currentUsername()).thenReturn(USERNAME);
        when(applicationRepository.save(any(Application.class))).thenAnswer(call -> call.getArgument(0));

        AddApplicationRequest request = request();
        request.setId(999L);

        assertEquals(null, applicationService.addApplication(request).getId());
    }

    private AddApplicationRequest request() {
        AddApplicationRequest request = new AddApplicationRequest();
        request.setName(DEFAULT_APP);
        request.setDescription(APP_DESCRIPTION);
        return request;
    }
}
