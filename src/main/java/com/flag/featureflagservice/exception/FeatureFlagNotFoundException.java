package com.flag.featureflagservice.exception;

public class FeatureFlagNotFoundException extends RuntimeException{
    public FeatureFlagNotFoundException(Long id){
        super("Invalid Feature Flag for id" + id);
    }
}
