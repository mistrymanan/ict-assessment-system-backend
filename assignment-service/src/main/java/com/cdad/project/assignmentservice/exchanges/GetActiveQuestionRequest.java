package com.cdad.project.assignmentservice.exchanges;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GetActiveQuestionRequest {
    @NotNull
    private String assignmentSlug;
    @NotNull
    private String questionSlug;
}