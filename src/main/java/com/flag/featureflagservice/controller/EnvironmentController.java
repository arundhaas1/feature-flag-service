package com.flag.featureflagservice.controller;

import com.flag.featureflagservice.controller.output.EnvironmentResponse;
import com.flag.featureflagservice.service.EnvironmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EnvironmentController {

    private final EnvironmentService environmentService;

    public EnvironmentController(EnvironmentService environmentService) {
        this.environmentService = environmentService;
    }

    @GetMapping("/environments")
    public List<EnvironmentResponse> getEnvironments() {
        return environmentService.getAll();
    }
}
