package com.cdad.project.gradingservice.exchange;

import com.cdad.project.gradingservice.entity.Language;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class PostSubmitRequest {
    private String assignmentId;
    private String questionId;
    private String sourceCode;
    private LocalDateTime timestamp;
    private Language language;
}
