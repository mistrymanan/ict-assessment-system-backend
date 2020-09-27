package com.cdad.project.gradingservice.serviceclient.executionservice.exchanges;

import com.cdad.project.gradingservice.dto.TestInput;
import com.cdad.project.gradingservice.entity.Language;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.TestCase;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PostBuildRequest {
    private String sourceCode;
    private List<TestCase> inputs;
    private Language language;
}