package com.flag.featureflagservice.controller;
import com.flag.featureflagservice.controller.input.AddFeatureFlagRequest;
import com.flag.featureflagservice.controller.input.AddOverrideRequest;
import com.flag.featureflagservice.controller.input.UpdateFeatureFlagRequest;
import com.flag.featureflagservice.controller.output.FeatureFlagResponse;
import com.flag.featureflagservice.controller.output.FeatureFlagStateResponse;
import com.flag.featureflagservice.controller.output.FlagOverrideResponse;
import com.flag.featureflagservice.model.FeatureFlag;
import com.flag.featureflagservice.model.FeatureFlagState;
import com.flag.featureflagservice.service.FeatureFlagService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/{application}/flags")
    public List<FeatureFlagStateResponse> getFlagList(@PathVariable String application,
                                                      @RequestParam Long environmentId) {
        return featureFlagService.getFlagList(application, environmentId);
    }

    @PostMapping("/{application}/flags")
    @ResponseStatus(HttpStatus.CREATED)
    public FeatureFlagResponse addFlag(@PathVariable String application,
                                       @Valid
                                       @RequestBody AddFeatureFlagRequest flagRequest){
        FeatureFlag flag =  featureFlagService.addFlag(application, flagRequest);
        return new FeatureFlagResponse(flag);
    }

    @PatchMapping("/{application}/flags/{flagId}")
    public FeatureFlagStateResponse updateFlag(@PathVariable String application,
                                               @PathVariable Long flagId,
                                               @Valid
                                               @RequestBody UpdateFeatureFlagRequest updateRequest) {
        return featureFlagService.updateFlag(application, flagId, updateRequest);
    }

    @DeleteMapping("/{application}/flags/{flagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFlag(@PathVariable String application,
                           @PathVariable Long flagId) {
        featureFlagService.deleteFlag(flagId);
    }

    @GetMapping("/{application}/evaluate")
    public boolean evaluateFlag(@PathVariable String application,
                                @RequestParam String flag,
                                @RequestParam String environment,
                                @RequestParam(required = false) String orgId){
        return featureFlagService.evaluate(flag, application, environment, orgId);
    }

    @GetMapping("/{application}/flags/{flagId}/overrides")
    public List<FlagOverrideResponse> getOverrides(@PathVariable String application,
                                                   @PathVariable Long flagId,
                                                   @RequestParam Long environmentId) {
        return featureFlagService.getOverrides(flagId, environmentId);
    }

    @PostMapping("/{application}/flags/{flagId}/overrides")
    @ResponseStatus(HttpStatus.CREATED)
    public FlagOverrideResponse addOverride(@PathVariable String application,
                                            @PathVariable Long flagId,
                                            @Valid @RequestBody AddOverrideRequest request) {
        return featureFlagService.addOverride(flagId, request);
    }

    @DeleteMapping("/{application}/flags/{flagId}/overrides/{overrideId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOverride(@PathVariable String application,
                               @PathVariable Long flagId,
                               @PathVariable Long overrideId) {
        featureFlagService.deleteOverride(overrideId);
    }
}