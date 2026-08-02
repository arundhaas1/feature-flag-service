package com.flag.featureflagservice.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "feature_flag_state", uniqueConstraints = @UniqueConstraint(columnNames = {"environment_id", "flag_id"}))
public class FeatureFlagState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flag_id", nullable = false)
    private FeatureFlag flag;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "environment_id", nullable = false)
    private Environment environment;
    @Column(nullable = false)
    private boolean enabled;
    @Column(nullable = false)
    @Version
    private int version;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public FeatureFlagState(Long id, FeatureFlag flag, Environment environment, boolean enabled, int version) {
        this.id = id;
        this.flag = flag;
        this.environment = environment;
        this.enabled = enabled;
        this.version = version;
    }
}
