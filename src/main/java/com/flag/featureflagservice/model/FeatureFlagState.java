package com.flag.featureflagservice.model;

public class FeatureFlagState {
    private Long id;
    private FeatureFlag flag;
    private Environment environment;
    private boolean enabled;
    private int version;

    public FeatureFlagState(Long id, FeatureFlag flag, Environment environment, boolean enabled, int version) {
        this.id = id;
        this.flag = flag;
        this.environment = environment;
        this.enabled = enabled;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public FeatureFlag getFlag() {
        return flag;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getVersion() {
        return version;
    }
}
