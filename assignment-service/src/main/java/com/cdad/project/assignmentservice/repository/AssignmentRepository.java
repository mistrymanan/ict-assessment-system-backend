package com.cdad.project.assignmentservice.repository;

import com.cdad.project.assignmentservice.entity.Assignment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends MongoRepository<Assignment, String> {
  Optional<Assignment> findBySlugAndEmail(String slug, String email);

  Optional<Assignment> findBySlug(String slug);

  Optional<Assignment> findBySlugAndStatus(String slug, String status);
  List<Assignment> findAllByStatusEquals(String status);

  List<Assignment> findAllByEmail(String email);

  void deleteByIdAndEmail(ObjectId id, String email);

  Optional<Assignment> findByIdAndEmail(ObjectId id, String email);
}
