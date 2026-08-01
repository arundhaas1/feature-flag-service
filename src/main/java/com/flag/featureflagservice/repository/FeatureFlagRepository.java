package com.flag.featureflagservice.repository;

import com.flag.featureflagservice.model.FeatureFlag;
import com.flag.featureflagservice.model.FeatureFlagState;

public interface FeatureFlagRepository {
    public FeatureFlag getFlag(Long id);
    public FeatureFlag addFlag(FeatureFlag flag);
    public FeatureFlagState updateFlag(FeatureFlagState flag);
    public FeatureFlagState[] getFlagList(FeatureFlagState flag);
    public void deleteFlag(FeatureFlagState flag);
}
