package com.flag.featureflagservice.repository;

import com.flag.featureflagservice.model.Environment;

public interface EnvironmentRepository {
    public Environment get(Long id);
    public Environment add(Environment environment);
}
