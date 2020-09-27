package com.cdad.project.gradingservice.repository;

import com.cdad.project.executionservice.entity.BuildEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BuildRepository extends MongoRepository<BuildEntity,String> {
}
