package com.flag.featureflagservice.exception;

public class OverrideNotFoundException extends RuntimeException {
    public OverrideNotFoundException(Long id) {
        super("Override not found with id " + id);
    }
}
