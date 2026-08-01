package com.flag.featureflagservice.controller;
import com.flag.featureflagservice.service.FeatureFlagService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class FeatureFlagController {
    FeatureFlagService featureFlagService; //Auto Injected

    public FeatureFlagController(FeatureFlagService featureFlagService){
        this.featureFlagService = featureFlagService;
        System.out.println("Constructor");
    }

    @GetMapping("/{app}/flags")
    public Map<String, Boolean> getFlagList(@PathVariable String app) {
//        return featureFlagService.getFlagList(app);
        return null;
    }

    @GetMapping("{app}/flags/{flagId}")
    public boolean getFlagList(@PathVariable String app,
                               @PathVariable Long flagId,
                               @RequestParam(required = false) String stage) { // False means mandatory
        return false;
    }

    @PostConstruct
    public void init() {
        System.out.println("PostConstruct");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("PreDestroy");
    }
}