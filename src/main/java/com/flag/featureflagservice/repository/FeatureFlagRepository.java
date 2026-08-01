package com.flag.featureflagservice.repository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;

@Repository
@ConfigurationProperties
public class FeatureFlagRepository {
    @Value("${spring.application.name}")
    private String appName;

    public Map<String, Boolean> getFlagList(String app){
        Map<String, Boolean> flagStatus = new HashMap<>();
        flagStatus.put(appName, false);
        return flagStatus;
    }
}
