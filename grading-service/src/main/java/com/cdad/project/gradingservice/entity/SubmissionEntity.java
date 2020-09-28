package com.cdad.project.gradingservice.entity;

import com.cdad.project.gradingservice.exchange.TestResult;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "submissions")
@Data
public class SubmissionEntity {
    @MongoId(FieldType.OBJECT_ID)
    private String id;
    private String assignmentId;
    private String questionId;
    private LocalDateTime timeStamp;
    private Status status;
    private Double score;
    private List<TestResult> testResults;
}
