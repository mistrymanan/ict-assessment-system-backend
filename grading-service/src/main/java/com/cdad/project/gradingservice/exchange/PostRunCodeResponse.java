package com.cdad.project.gradingservice.exchange;

import com.cdad.project.gradingservice.entity.Status;
import lombok.Data;

@Data
public class PostRunCodeResponse {
    private String input;
    private String output;
    private long executionTime;
    private String expectedOutput;
    private Status status;
}
