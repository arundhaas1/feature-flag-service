package com.flag.featureflagservice.repository;

import com.flag.featureflagservice.model.Environment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvironmentRepository extends JpaRepository<Environment, Long> {
    boolean existsByName(String name);
}
