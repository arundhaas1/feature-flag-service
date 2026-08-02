package com.flag.featureflagservice.controller.input;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateFeatureFlagRequest {
    @NotNull(message = "enabled must be true or false")
    private Boolean enabled;
    @NotNull(message = "Environment Id cannot be empty")
    private Long environmentId;
}
