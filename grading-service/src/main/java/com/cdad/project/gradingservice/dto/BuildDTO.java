package com.cdad.project.gradingservice.dto;

import com.cdad.project.gradingservice.entity.Language;
import com.cdad.project.gradingservice.entity.Status;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
public class BuildDTO {
    @MongoId
    private String id;
    private Status status;
    private List<TestCase> testOutputs;
    private String sourceCode;
    private long executionTime;
    private LocalDateTime timeStamp;
    private Language language;
    private String error;
}