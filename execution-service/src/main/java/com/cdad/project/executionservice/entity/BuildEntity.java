package com.cdad.project.executionservice.entity;

import com.cdad.project.executionservice.dto.TestOutput;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime timeStamp;
    private Language language;
    private String error;
}
