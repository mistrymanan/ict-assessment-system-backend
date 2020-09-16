package com.cdad.project.executionservice.repository;

import com.cdad.project.executionservice.entity.BuildEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;


@Repository
public interface BuildRepository extends MongoRepository<BuildEntity,String> {
}
