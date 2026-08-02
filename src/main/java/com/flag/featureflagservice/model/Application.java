package com.flag.featureflagservice.model;
import com.flag.featureflagservice.controller.input.AddApplicationRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, unique = true, length = 30)
    @Pattern(regexp = "^[A-Za-z0-9]+$")
    private String name;
    @Column(name = "description", length = 200)
    private String description;
    @Column(name = "created_by", nullable = false)
    private String createdBy;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;


    //temp
    public Application(AddApplicationRequest addApplicationRequest){
        this.id = addApplicationRequest.getId();
        this.name = addApplicationRequest.getName();
        this.description = addApplicationRequest.getDescription();
        this.createdBy = "Arun";
        this.createdAt = Instant.now();
    }
}
