package com.flag.featureflagservice.repository;

import com.flag.featureflagservice.model.FlagOverride;
import com.flag.featureflagservice.model.OverrideScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FlagOverrideRepository extends JpaRepository<FlagOverride, Long> {

    List<FlagOverride> findByFlagIdAndEnvironmentId(Long flagId, Long environmentId);

    void deleteByFlagId(Long flagId);

    /** Loaded by name, like the evaluation query, so no ids are needed to build a cache entry. */
    @Query("""
            select o from FlagOverride o
            where o.flag.flagKey = :flagKey
              and o.flag.application.name = :appName
              and o.environment.name = :envName
              and o.scope = :scope
            """)
    List<FlagOverride> findForEvaluation(@Param("flagKey") String flagKey,
                                         @Param("appName") String appName,
                                         @Param("envName") String envName,
                                         @Param("scope") OverrideScope scope);
}
