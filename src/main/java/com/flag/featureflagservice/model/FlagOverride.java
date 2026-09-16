package com.flag.featureflagservice.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * An exception to a flag's environment default, for one org.
 *
 * <p>Absence of a row means "follow the environment default" — overrides are the exception, not
 * the rule, which is what keeps {@link FeatureFlagState} meaningful and the cache small.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "flag_override",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"flag_id", "environment_id", "scope", "scope_value"}))
public class FlagOverride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flag_id", nullable = false)
    private FeatureFlag flag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "environment_id", nullable = false)
    private Environment environment;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope", nullable = false, length = 20)
    private OverrideScope scope;

    @Column(name = "scope_value", nullable = false, length = 64)
    private String scopeValue;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    public FlagOverride(Long id, FeatureFlag flag, Environment environment, OverrideScope scope,
                        String scopeValue, boolean enabled, Instant createdAt, String createdBy) {
        this.id = id;
        this.flag = flag;
        this.environment = environment;
        this.scope = scope;
        this.scopeValue = scopeValue;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }
}
