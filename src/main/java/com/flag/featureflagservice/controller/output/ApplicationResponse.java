package com.flag.featureflagservice.controller.output;
import com.flag.featureflagservice.model.Application;
import lombok.Getter;

import java.time.Instant;

@Getter
public class ApplicationResponse {
    private Long id;
    private String name;
    private String description;
    private String createdBy;
    private Instant createdAt;

    public ApplicationResponse(Application application) {
        this.id = application.getId();
        this.name = application.getName();
        this.description = application.getDescription();
        this.createdBy = application.getCreatedBy();
        this.createdAt = application.getCreatedAt();
    }
}
