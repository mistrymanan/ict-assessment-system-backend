package com.cdad.project.executionservice.entity;

import com.cdad.project.executionservice.dto.TestOutput;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "builds")
@Data
@ToString
public class BuildEntity {
    @MongoId
    private String id;
    private Status status;
    private List<TestOutput> testOutputs;
    private String sourceCode;
    private long executionTime;
    private LocalDateTime timeStamp;
    private Language language;
    private String error;
}
