package com.flag.featureflagservice.controller.output;

import com.flag.featureflagservice.model.Application;
import com.flag.featureflagservice.model.FeatureFlag;
import lombok.Data;
import java.time.Instant;

@Data
public class FeatureFlagResponse {
    private Long id;
    private String flagKey;
    private String description;
    private Application application;
    private Instant createdAt;
    private String createdBy;

    public FeatureFlagResponse(FeatureFlag flag) {
        this.id = flag.getId();
        this.flagKey = flag.getFlagKey();
        this.description = flag.getDescription();
        this.application = flag.getApplication();
        this.createdAt = flag.getCreatedAt();
        this.createdBy = flag.getCreatedBy();
    }
}
