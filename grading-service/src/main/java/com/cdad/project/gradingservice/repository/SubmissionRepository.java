package com.cdad.project.gradingservice.repository;

import com.cdad.project.gradingservice.entity.SubmissionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SubmissionRepository extends MongoRepository<SubmissionEntity, String> {
    List<SubmissionEntity> findAllByClassroomSlugAndEmail(String classroomSlug, String email);

    List<SubmissionEntity> findAllByAssignmentId(String assignmentId);

    List<SubmissionEntity> findAllByClassroomSlugAndAssignmentId(String classroomSlug, String assignmentId);

    SubmissionEntity findByAssignmentIdAndEmail(String assignmentId, String email);

    SubmissionEntity findByClassroomSlugAndAssignmentIdAndEmail(String classroomSlug, String assignmentId, String email);

    boolean existsByAssignmentIdAndEmail(String assignmentId, String email);

    boolean existsByClassroomSlugAndAssignmentIdAndEmail(String classroomSlug, String assignmentId, String email);

}
