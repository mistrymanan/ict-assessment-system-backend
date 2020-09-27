package com.cdad.project.gradingservice.exchange;

import com.cdad.project.gradingservice.entity.Language;
import lombok.Data;

@Data
public class PostRunCodeRequest {
    private String assignmentId;
    private String questionId;
    private Language language;
    private String input;
    private String sourceCode;
}
