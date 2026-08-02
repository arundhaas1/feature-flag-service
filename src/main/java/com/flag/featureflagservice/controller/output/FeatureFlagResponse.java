package com.flag.featureflagservice.controller.output;

import com.flag.featureflagservice.model.FeatureFlag;
import lombok.Data;
import java.time.Instant;

@Data
public class FeatureFlagResponse {
    private Long id;
    private String flagKey;
    private String description;
    private Long applicationId;
    private String applicationName;
    private Instant createdAt;
    private String createdBy;

    public FeatureFlagResponse(FeatureFlag flag) {
        this.id = flag.getId();
        this.flagKey = flag.getFlagKey();
        this.description = flag.getDescription();
        this.applicationId = flag.getApplication().getId();
        this.applicationName = flag.getApplication().getName();
        this.createdAt = flag.getCreatedAt();
        this.createdBy = flag.getCreatedBy();
    }
}
