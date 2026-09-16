package com.flag.featureflagservice.controller.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Note there is no {@code id}: the database assigns it. A client that sends one is ignored
 * rather than allowed to choose its own primary key.
 */
@Data
public class AddApplicationRequest {

    @NotBlank(message = "Application name cannot be empty")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Application name must contain only letters and digits")
    @Size(max = 30, message = "Application name cannot exceed 30 characters")
    private String name;

    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;
}
