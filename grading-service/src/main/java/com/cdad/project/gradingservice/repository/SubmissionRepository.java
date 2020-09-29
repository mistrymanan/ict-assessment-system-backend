package com.cdad.project.gradingservice.repository;

import com.cdad.project.gradingservice.entity.SubmissionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SubmissionRepository extends MongoRepository<SubmissionEntity,String> {
    List<SubmissionEntity> findAllByAssignmentIdAndQuestionId(String assignmentId, String questionId);
}
