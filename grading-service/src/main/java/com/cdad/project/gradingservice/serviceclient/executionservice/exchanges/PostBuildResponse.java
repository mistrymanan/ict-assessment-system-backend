package com.cdad.project.gradingservice.serviceclient.executionservice.exchanges;


import com.cdad.project.gradingservice.serviceclient.executionservice.dto.TestCaseResult;
import com.cdad.project.gradingservice.entity.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PostBuildResponse {
    private String id;
    private Status status;
    private List<TestCaseResult> testOutputs;
    private String message;
    private long executionTime;
}