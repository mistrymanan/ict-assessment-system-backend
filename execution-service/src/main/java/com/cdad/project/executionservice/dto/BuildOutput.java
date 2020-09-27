package com.cdad.project.executionservice.dto;

import com.cdad.project.executionservice.entity.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BuildOutput {
    private String id;
    private Status status;
    private List<TestOutput> testOutputs;
    private long executionTime;
}