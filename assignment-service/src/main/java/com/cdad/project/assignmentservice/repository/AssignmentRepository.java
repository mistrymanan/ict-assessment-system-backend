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

    boolean existsBySlugAndClassroomSlug(String slug, String classroomSlug);

//  Optional<Assignment> findBySlugAndEmail(String slug, String email);

    Optional<Assignment> findBySlug(String slug);

    Optional<Assignment> findBySlugAndClassroomSlug(String slug, String classroomSlug);

    Optional<Assignment> findBySlugAndStatus(String slug, String status);

    Optional<Assignment> findBySlugAndStatusAndClassroomSlug(String slug, String status, String classroomSlug);

    List<Assignment> findAllByStatusEquals(String status);

    List<Assignment> findAllByClassroomSlugEqualsAndStatusEquals(String classroomSlug, String status);

    //List<Assignment> findAllByEmail(String email);

    List<Assignment> findAllByClassroomSlugEquals(String classroomSlug);

    //void deleteByIdAndEmail(ObjectId id, String email);

    void deleteByIdAndClassroomSlug(ObjectId id, String classroomSlug);

    void deleteAssignmentBySlugAndClassroomSlug(String slug, String classroomSlug);
    //Optional<Assignment> findByIdAndEmail(ObjectId id, String email);

    Optional<Assignment> findByIdAndClassroomSlug(ObjectId id, String classroomSlug);

    Optional<Assignment> findByIdAndStatus(ObjectId id, String status);

    Optional<Assignment> findByIdAndStatusAndClassroomSlug(ObjectId id, String status, String classroomSlug);
}
