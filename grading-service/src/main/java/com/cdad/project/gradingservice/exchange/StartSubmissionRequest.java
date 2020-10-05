package com.cdad.project.gradingservice.exchange;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StartSubmissionRequest {
    @NotNull
    private String email;
    @NotNull
    private String assignmentId;
}
