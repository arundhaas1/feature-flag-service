package com.flag.featureflagservice.repository;

import com.flag.featureflagservice.model.Environment;
import com.flag.featureflagservice.model.FeatureFlag;
import com.flag.featureflagservice.model.FeatureFlagState;

public interface FeatureFlagRepository {
    public FeatureFlagState getFlag(Long id, Environment environment);
    public FeatureFlag addFlag(FeatureFlag flag);
    public FeatureFlagState updateFlag(FeatureFlagState flag);
    public FeatureFlagState[] getFlagList(FeatureFlagState flag);
    public void deleteFlag(FeatureFlagState flag);
}
