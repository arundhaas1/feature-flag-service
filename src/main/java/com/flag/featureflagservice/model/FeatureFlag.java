package com.flag.featureflagservice.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "feature_flag", uniqueConstraints = @UniqueConstraint(columnNames = {"application_id", "flag_key"}))
public class FeatureFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "flag_key", length = 100, nullable = false)
    private String flagKey;
    @Column(name = "description", nullable = false)
    private String description;
    @ManyToOne(fetch = FetchType.LAZY) //Many FF can connect to same application
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "created_by", nullable = false)
    private String createdBy;

    public FeatureFlag(Long id, String flagKey, String description, Application application, Instant createdAt, String createdBy) {
        this.id = id;
        this.flagKey = flagKey;
        this.description = description;
        this.application = application;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }
}
