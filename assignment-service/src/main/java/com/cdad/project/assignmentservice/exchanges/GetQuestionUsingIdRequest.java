package com.cdad.project.assignmentservice.exchanges;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GetQuestionUsingIdRequest {
    @NotNull
    private String assignmentId;
    @NotNull
    private String questionId;
}
