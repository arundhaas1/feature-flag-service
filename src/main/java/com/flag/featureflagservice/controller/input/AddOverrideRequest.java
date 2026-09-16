package com.flag.featureflagservice.controller.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * There is no {@code scope} field: only org-level overrides are evaluated today, so accepting a
 * scope would let a caller create a DC override that silently never applies.
 */
@Data
public class AddOverrideRequest {

    @NotBlank(message = "Organisation id cannot be empty")
    @Size(max = 64, message = "Organisation id cannot exceed 64 characters")
    private String orgId;

    @NotNull(message = "enabled must be true or false")
    private Boolean enabled;

    @NotNull(message = "Environment Id cannot be empty")
    private Long environmentId;
}
