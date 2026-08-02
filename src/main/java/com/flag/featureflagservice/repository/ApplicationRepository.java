package com.flag.featureflagservice.repository;

import com.flag.featureflagservice.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findByName(String name);

    default Application add(Application application){
        return save(application);
    }
}