package com.flag.featureflagservice.model;

import java.time.Instant;

public class Environment {
    private Long id;
    private Env envName;
    private String description;
    private Instant addedAt;
    private String addedBy;

    public Environment(Long id, Env envName, String description, Instant addedAt, String addedBy) {
        this.id = id;
        this.envName = envName;
        this.description = description;
        this.addedAt = addedAt;
        this.addedBy = addedBy;
    }

    public Long getId() {
        return id;
    }

    public Env getEnvName() {
        return envName;
    }

    public String getDescription() {
        return description;
    }

    public Instant getAddedAt() {
        return addedAt;
    }

    public String getAddedBy() {
        return addedBy;
    }
}
