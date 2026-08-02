package com.flag.featureflagservice.controller;
import com.flag.featureflagservice.controller.input.AddFeatureFlagRequest;
import com.flag.featureflagservice.controller.output.FeatureFlagResponse;
import com.flag.featureflagservice.controller.output.FeatureFlagStateResponse;
import com.flag.featureflagservice.model.FeatureFlag;
import com.flag.featureflagservice.model.FeatureFlagState;
import com.flag.featureflagservice.service.FeatureFlagService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class FeatureFlagController {
    private final FeatureFlagService featureFlagService;

    public FeatureFlagController(FeatureFlagService featureFlagService){
        this.featureFlagService = featureFlagService;
    }

    @GetMapping("/{application}/flags/{flagId}")
    public FeatureFlagStateResponse getFlag(@PathVariable String application,
                                        @PathVariable Long flagId,
                                        @RequestParam Long environmentId) {
        return featureFlagService.getFlag(application, flagId, environmentId);
    }

    @PostMapping("/{application}/flags")
    public FeatureFlagResponse addFlag(@PathVariable String application,
                                       @Valid
                                       @RequestBody AddFeatureFlagRequest flagRequest){
        FeatureFlag flag =  featureFlagService.addFlag(application, flagRequest);
        return new FeatureFlagResponse(flag);
    }
}