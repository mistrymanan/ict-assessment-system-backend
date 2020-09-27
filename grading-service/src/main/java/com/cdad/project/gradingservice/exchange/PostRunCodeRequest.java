package com.cdad.project.gradingservice.exchange;

import com.cdad.project.gradingservice.entity.Language;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PostRunCodeRequest {
    @NotNull
    private String assignmentId;
    @NotNull
    private String questionId;
    @NotNull
    private Language language;
    private String input;
    @NotNull
    private String sourceCode;
}
