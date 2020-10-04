package com.cdad.project.gradingservice.serviceclient.executionservice.dto;

import com.cdad.project.gradingservice.entity.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestCaseResult {
    String id;
    String input;
    String output;
    Status status;
    long executionTime;
}