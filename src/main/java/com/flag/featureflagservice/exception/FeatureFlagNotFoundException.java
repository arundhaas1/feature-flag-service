package com.flag.featureflagservice.exception;

public class FeatureFlagNotFoundException extends RuntimeException{
    public FeatureFlagNotFoundException(Long id){
        super("Feature flag not found with id " + id);
    }
}
