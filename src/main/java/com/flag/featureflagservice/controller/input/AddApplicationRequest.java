package com.flag.featureflagservice.controller.input;
import lombok.Data;

@Data
public class AddApplicationRequest {
    private Long id;
    private String name;
    private String description;
}
