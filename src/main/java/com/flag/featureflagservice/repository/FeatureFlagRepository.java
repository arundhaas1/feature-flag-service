package com.flag.featureflagservice.repository;

import com.flag.featureflagservice.model.FeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, Long> {
    Optional<FeatureFlag> findByApplicationIdAndFlagKey(Long applicationId, String flagKey);
}
