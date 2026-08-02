package com.flag.featureflagservice.exception;

public class ApplicationNotFoundException extends RuntimeException{
    public ApplicationNotFoundException(Long id){
        super("Application not found with id " + id);
    }

    public ApplicationNotFoundException(String name){
        super("Application not found with name " + name);
    }
}
