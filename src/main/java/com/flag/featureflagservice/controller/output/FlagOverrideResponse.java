package com.flag.featureflagservice.controller.output;

import com.flag.featureflagservice.model.FlagOverride;
import lombok.Getter;

import java.time.Instant;

@Getter
public class FlagOverrideResponse {
    private final Long id;
    private final Long flagId;
    private final String flagKey;
    private final Long environmentId;
    private final String environment;
    private final String scope;
    private final String scopeValue;
    private final boolean enabled;
    private final String createdBy;
    private final Instant createdAt;

    public FlagOverrideResponse(FlagOverride override) {
        this.id = override.getId();
        this.flagId = override.getFlag().getId();
        this.flagKey = override.getFlag().getFlagKey();
        this.environmentId = override.getEnvironment().getId();
        this.environment = override.getEnvironment().getName();
        this.scope = override.getScope().name();
        this.scopeValue = override.getScopeValue();
        this.enabled = override.isEnabled();
        this.createdBy = override.getCreatedBy();
        this.createdAt = override.getCreatedAt();
    }
}
