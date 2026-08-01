package com.flag.featureflagservice.repository;

import com.flag.featureflagservice.model.Environment;

interface EnvironmentRepository {
    public Environment getEnvironment(Long id);
}
