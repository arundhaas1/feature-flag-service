package com.flag.featureflagservice.service;

import com.flag.featureflagservice.controller.output.EnvironmentResponse;
import com.flag.featureflagservice.model.Environment;
import com.flag.featureflagservice.repository.EnvironmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnvironmentService {

    private final EnvironmentRepository environmentRepository;

    public EnvironmentService(EnvironmentRepository environmentRepository) {
        this.environmentRepository = environmentRepository;
    }

    public List<EnvironmentResponse> getAll() {
        return environmentRepository.findAll().stream()
                .map(EnvironmentService::toResponse)
                .toList();
    }

    private static EnvironmentResponse toResponse(Environment environment) {
        return new EnvironmentResponse(environment.getId(), environment.getName(),
                environment.getDescription());
    }
}
