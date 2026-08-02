package com.flag.featureflagservice.controller;

import com.flag.featureflagservice.controller.input.AddApplicationRequest;
import com.flag.featureflagservice.controller.output.ApplicationResponse;
import com.flag.featureflagservice.model.Application;
import com.flag.featureflagservice.service.ApplicationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService){
        this.applicationService = applicationService;
    }

    @PostMapping("/application")
    public ApplicationResponse addApplication(@RequestBody AddApplicationRequest addApplicationRequest){
        Application application =  applicationService.addApplication(addApplicationRequest);
        return new ApplicationResponse(application);
    }
}