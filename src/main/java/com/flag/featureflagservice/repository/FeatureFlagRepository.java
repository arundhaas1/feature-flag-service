package com.flag.featureflagservice.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class FeatureFlagRepository {
    public Map<String, Boolean> getFlagList(String app){
        Map<String, Boolean> flagStatus = new HashMap<>();
        flagStatus.put(app, false);
        return flagStatus;
    }
}
