package com.flag.featureflagservice.exception;

public class EnvironmentNotFoundException extends RuntimeException {
    public EnvironmentNotFoundException(Long id){
        super("Environment not found with id" + id);
    }
}
