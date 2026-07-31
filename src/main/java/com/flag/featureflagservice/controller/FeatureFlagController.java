package com.flag.featureflagservice.controller;
import com.flag.featureflagservice.service.FeatureFlagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class FeatureFlagController {
    FeatureFlagService featureFlagService; //Auto Injected
    public FeatureFlagController(FeatureFlagService featureFlagService){
        this.featureFlagService = featureFlagService;
    }

    @GetMapping("/{app}/flags")
    public Map<String, Boolean> getFlagList(@PathVariable String app) {
        return featureFlagService.getFlagList(app);
    }
}