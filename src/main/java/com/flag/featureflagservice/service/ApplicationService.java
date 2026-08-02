package com.flag.featureflagservice.service;

import com.flag.featureflagservice.controller.input.AddApplicationRequest;
import com.flag.featureflagservice.model.Application;
import com.flag.featureflagservice.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository){
        this.applicationRepository = applicationRepository;
    }

    public Application addApplication(AddApplicationRequest addApplicationRequest) {
        Application application = new Application(addApplicationRequest);
        return applicationRepository.add(application);
    }
}
