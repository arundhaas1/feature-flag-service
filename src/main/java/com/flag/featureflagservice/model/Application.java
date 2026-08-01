package com.flag.featureflagservice.model;

import java.time.Instant;

public class Application {
    private Long id;
    private String name;
    private String description;
    private String createdBy;
    private Instant createdAt;

    public Application(Long id, String name, String description, String createdBy, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
