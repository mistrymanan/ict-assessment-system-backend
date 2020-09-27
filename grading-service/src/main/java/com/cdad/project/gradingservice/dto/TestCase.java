package com.cdad.project.gradingservice.dto;

import com.cdad.project.gradingservice.entity.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestCase {
    String id;
    String input;
    String output;
    Status status;
    long executionTime;
}