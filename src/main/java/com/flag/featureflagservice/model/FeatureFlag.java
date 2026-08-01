package com.flag.featureflagservice.model;

import java.time.Instant;

public class FeatureFlag {
    private Long id;
    private String flagKey;
    private String description;
    private Application application;
    private Instant createdAt;
    private String createdBy;

    public FeatureFlag(Long id, String flagKey, String description, Application application, Instant createdAt, String createdBy) {
        this.id = id;
        this.flagKey = flagKey;
        this.description = description;
        this.application = application;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public String getFlagKey() {
        return flagKey;
    }

    public String getDescription() {
        return description;
    }

    public Application getApplication() {
        return application;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
