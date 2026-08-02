package com.flag.featureflagservice.controller.output;

import com.flag.featureflagservice.model.Env;
import com.flag.featureflagservice.model.FeatureFlagState;
import lombok.Getter;

@Getter
public class FeatureFlagStateResponse {
    private final Long id;
    private final Long flagId;
    private final String flagKey;
    private final Long environmentId;
    private final Env environment;
    private final boolean enabled;
    private final int version;

    public FeatureFlagStateResponse(FeatureFlagState state) {
        this.id = state.getId();
        this.flagId = state.getFlag().getId();
        this.flagKey = state.getFlag().getFlagKey();
        this.environmentId = state.getEnvironment().getId();
        this.environment = state.getEnvironment().getEnvName();
        this.enabled = state.isEnabled();
        this.version = state.getVersion();
    }
}
