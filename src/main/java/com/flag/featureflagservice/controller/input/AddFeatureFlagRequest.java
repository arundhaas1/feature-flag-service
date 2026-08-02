package com.flag.featureflagservice.controller.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddFeatureFlagRequest {
    @NotBlank(message = "Flag name cannot be empty")
    private String name;
    @Size(max = 200)
    private String description;
    @NotEmpty(message = "Environment Id cannot be empty")
    private Long[] environmentId;
}
