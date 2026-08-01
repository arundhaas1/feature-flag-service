package com.flag.featureflagservice.repository;

import com.flag.featureflagservice.model.Application;

public interface ApplicationRepository{
    public Application get(Long id);
    public Application add(Application application);
}