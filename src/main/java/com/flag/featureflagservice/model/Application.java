package com.flag.featureflagservice.model;
import com.flag.featureflagservice.controller.input.AddApplicationRequest;
import jakarta.persistence.*;
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
    @Column(nullable = false, unique = true, length = 30)
    private String name;
    @Column(length = 200)
    private String description;
    @Column(nullable = false)
    private String createdBy;
    @Column(nullable = false, updatable = false)
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
