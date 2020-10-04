package com.cdad.project.gradingservice.exchange;

import com.cdad.project.gradingservice.entity.Language;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class PostSubmitRequest {
    @NotNull
    private String assignmentId;
    @NotNull
    private String questionId;
    @NotNull
    private String email;
    @NotNull
    private String sourceCode;
    @NotNull
    private Language language;
}
