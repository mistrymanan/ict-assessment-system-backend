package com.cdad.project.gradingservice.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(collection = "submissions")
@Data
public class SubmissionEntity {
    @MongoId
    private UUID id;
    private String assignmentId;
    private String email;
    private List<QuestionEntity> questionEntities;
    private SubmissionStatus submissionStatus;
    private LocalDateTime startOn;
    private LocalDateTime completedOn;
    private Double assignmentScore;
    private Double currentScore;
}