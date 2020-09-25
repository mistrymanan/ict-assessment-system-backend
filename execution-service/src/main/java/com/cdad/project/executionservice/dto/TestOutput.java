package com.cdad.project.executionservice.dto;

import com.cdad.project.executionservice.entity.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestOutput {
    String id;
    String input;
    String output;
    Status status;
    long executionTime;
}