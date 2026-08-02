package com.flag.featureflagservice.config;

import com.flag.featureflagservice.model.Environment;
import com.flag.featureflagservice.repository.EnvironmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class EnvironmentSeeder implements CommandLineRunner {
    private static final List<String> DEFAULT_ENVIRONMENTS =
            List.of("DEV", "Local", "QA", "PreLive", "Production");

    private final EnvironmentRepository environmentRepository;

    public EnvironmentSeeder(EnvironmentRepository environmentRepository) {
        this.environmentRepository = environmentRepository;
    }

    @Override
    public void run(String... args) {
        for (String name : DEFAULT_ENVIRONMENTS) {
            if (!environmentRepository.existsByName(name)) {
                environmentRepository.save(
                        new Environment(null, name, name + " environment", Instant.now(), "system"));
            }
        }
    }
}
