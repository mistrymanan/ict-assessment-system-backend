package com.cdad.project.assignmentservice.repository;

import com.cdad.project.assignmentservice.entity.AssignmentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository extends MongoRepository<AssignmentEntity, String> {
}
