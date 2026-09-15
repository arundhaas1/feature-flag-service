package com.flag.featureflagservice.config;

import com.flag.featureflagservice.model.Application;
import com.flag.featureflagservice.repository.ApplicationRepository;
import com.flag.featureflagservice.security.CurrentUserProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Creates the single application the admin page works against.
 *
 * <p>The POC deliberately has no per-application isolation, but flags still belong to an
 * application in the schema. Seeding one keeps that column satisfied without the UI having to
 * ask which application the user means — switching isolation on later means letting the UI pick
 * an application instead of assuming this one.
 */
@Component
public class DefaultApplicationSeeder implements CommandLineRunner {

    public static final String DEFAULT_APPLICATION_NAME = "default";
    private static final String DESCRIPTION = "Default application for the feature flag POC";

    private final ApplicationRepository applicationRepository;

    public DefaultApplicationSeeder(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Override
    public void run(String... args) {
        if (applicationRepository.findByName(DEFAULT_APPLICATION_NAME).isEmpty()) {
            applicationRepository.save(new Application(null, DEFAULT_APPLICATION_NAME, DESCRIPTION,
                    CurrentUserProvider.SYSTEM, Instant.now()));
        }
    }
}
