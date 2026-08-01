package com.flag.featureflagservice.repository;

import com.flag.featureflagservice.model.Env;
import com.flag.featureflagservice.model.Environment;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class InMemoryEnvironmentRepository implements EnvironmentRepository{
    private final Map<Long, Environment> environments = new HashMap<>();

    public InMemoryEnvironmentRepository(){
        environments.put(31L, getDummyEnv(31L, Env.DEV));
        environments.put(32L, getDummyEnv(32L, Env.Local));
        environments.put(33L, getDummyEnv(33L, Env.PreLive));
        environments.put(34L, getDummyEnv(34L, Env.Production));
    }

    @Override
    public Environment get(Long id) {
        return null;
    }

    @Override
    public Environment add(Environment environment) {
        return environments.put(environment.getId(), environment);
    }

    private Environment getDummyEnv(Long id, Env env){
        return new Environment(id, env, "", Instant.now(), "");
    }
}
