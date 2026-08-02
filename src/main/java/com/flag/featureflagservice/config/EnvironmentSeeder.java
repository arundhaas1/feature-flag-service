package com.flag.featureflagservice.config;

import com.flag.featureflagservice.model.Env;
import com.flag.featureflagservice.model.Environment;
import com.flag.featureflagservice.repository.EnvironmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class EnvironmentSeeder implements CommandLineRunner {
    private final EnvironmentRepository environmentRepository;

    public EnvironmentSeeder(EnvironmentRepository environmentRepository) {
        this.environmentRepository = environmentRepository;
    }

    @Override
    public void run(String... args) {
        for (Env env : Env.values()) {
            if (!environmentRepository.existsByEnvName(env)) {
                environmentRepository.save(
                        new Environment(null, env, env.name() + " environment", Instant.now(), "system"));
            }
        }
    }
}
