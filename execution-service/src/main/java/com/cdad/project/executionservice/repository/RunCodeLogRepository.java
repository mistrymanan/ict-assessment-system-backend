package com.cdad.project.executionservice.repository;

import com.cdad.project.executionservice.entity.RunCodeLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RunCodeLogRepository extends MongoRepository<RunCodeLog, String> {
}
