package com.cdad.project.assignmentservice.repository;

import com.cdad.project.assignmentservice.entity.Assignment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends MongoRepository<Assignment, String> {
  Optional<Assignment> findBySlug(String slug);
  List<Assignment> findAllByStatusEquals(String status);
}
