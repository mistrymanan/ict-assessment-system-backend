package com.cdad.project.gradingservice.exchange;

import com.cdad.project.gradingservice.entity.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostRunCodeResponse {
    private String input;
    private String output;
    private long executionTime;
    private String expectedOutput;
    private Status status;
}
