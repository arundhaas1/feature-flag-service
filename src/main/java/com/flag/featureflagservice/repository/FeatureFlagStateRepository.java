package com.flag.featureflagservice.repository;

import com.flag.featureflagservice.model.FeatureFlagState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FeatureFlagStateRepository extends JpaRepository<FeatureFlagState, Long> {
    Optional<FeatureFlagState> findByFlagIdAndEnvironmentId(Long flagId, Long environmentId);
    List<FeatureFlagState> findByFlagApplicationIdAndEnvironmentId(Long applicationId, Long environmentId);
    void deleteByFlagId(Long flagId);

    @Query("""
            select s from FeatureFlagState s
            where s.flag.flagKey = :flagKey
              and s.flag.application.name = :appName
              and s.environment.name = :envName
            """)
    Optional<FeatureFlagState> findForEvaluation(@Param("flagKey") String flagKey,
                                                 @Param("appName") String appName,
                                                 @Param("envName") String envName);
}
