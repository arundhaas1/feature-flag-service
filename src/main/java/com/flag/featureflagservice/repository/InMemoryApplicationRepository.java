package com.flag.featureflagservice.repository;

import com.flag.featureflagservice.model.Application;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryApplicationRepository implements ApplicationRepository{
    private final Map<Long, Application> applications = new HashMap<>();

    public InMemoryApplicationRepository(){
        applications.put(1L, getDummyApp(1L, "Zoho Desk"));
        applications.put(2L, getDummyApp(2L, "Zoho Pay"));
        applications.put(3L, getDummyApp(3L, "Zoho Meeting"));
        applications.put(4L, getDummyApp(4L, "Zoho Writer"));
        applications.put(5L, getDummyApp(5L, "Zoho Crm"));
        applications.put(6L, getDummyApp(6L, "Zoho Clock"));
    }

    @Override
    public Application get(Long id) {
        return applications.get(id);
    }

    @Override
    public Application add(Application application) {
        return applications.put(application.getId(), application);
    }

    private Application getDummyApp(Long id, String name){
        return new Application(id, name, name, "Arun", Instant.now());
    }
}