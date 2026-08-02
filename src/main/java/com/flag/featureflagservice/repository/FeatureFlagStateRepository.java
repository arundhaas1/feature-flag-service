package com.flag.featureflagservice.repository;

import com.flag.featureflagservice.model.FeatureFlagState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeatureFlagStateRepository extends JpaRepository<FeatureFlagState, Long> {
    Optional<FeatureFlagState> findByFlagIdAndEnvironmentId(Long flagId, Long environmentId);
    List<FeatureFlagState> findByFlagApplicationIdAndEnvironmentId(Long applicationId, Long environmentId);
    void deleteByFlagId(Long flagId);
}
