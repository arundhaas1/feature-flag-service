package com.flag.featureflagservice.service;

import com.flag.featureflagservice.controller.input.AddApplicationRequest;
import com.flag.featureflagservice.model.Application;
import com.flag.featureflagservice.repository.ApplicationRepository;
import com.flag.featureflagservice.security.CurrentUserProvider;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final CurrentUserProvider currentUserProvider;

    public ApplicationService(ApplicationRepository applicationRepository,
                              CurrentUserProvider currentUserProvider){
        this.applicationRepository = applicationRepository;
        this.currentUserProvider = currentUserProvider;
    }

    public Application addApplication(AddApplicationRequest addApplicationRequest) {
        // The id is deliberately not taken from the request: it is assigned by the database.
        Application application = new Application(null,
                addApplicationRequest.getName(),
                addApplicationRequest.getDescription(),
                currentUserProvider.currentUsername(),
                Instant.now());
        return applicationRepository.save(application);
    }
}
