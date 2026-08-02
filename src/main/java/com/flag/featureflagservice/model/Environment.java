package com.flag.featureflagservice.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "environment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Environment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "name", length = 100, unique = true, nullable = false)
    private Env envName;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "created_at", nullable = false)
    private Instant addedAt;
    @Column(name = "created_by", nullable = false)
    private String addedBy;

    public Environment(Long id, Env envName, String description, Instant addedAt, String addedBy) {
        this.id = id;
        this.envName = envName;
        this.description = description;
        this.addedAt = addedAt;
        this.addedBy = addedBy;
    }

}
