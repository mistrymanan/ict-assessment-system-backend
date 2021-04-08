package com.cdad.project.plagiarismservice.repository;

import com.cdad.project.plagiarismservice.entity.Plagiarism;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlagiarismRepository extends MongoRepository<Plagiarism,String> {
    Plagiarism getPlagiarismById(String id);
    List<Plagiarism> getPlagiarismByClassroomSlugAndAssignmentIdAndQuestionId(String classroomSlug
    ,String assignmentId,String questionId);
}
