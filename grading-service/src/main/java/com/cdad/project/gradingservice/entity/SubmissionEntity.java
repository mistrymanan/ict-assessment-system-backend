package com.cdad.project.gradingservice.entity;

import com.cdad.project.gradingservice.exchange.TestResult;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
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
    private String questionId;
    private LocalDateTime time;
    private SubmissionStatus submissionStatus;
    private ResultStatus status;
    private Double score;
    private List<TestResult> testResults;
}