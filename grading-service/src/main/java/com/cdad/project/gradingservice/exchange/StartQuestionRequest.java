package com.cdad.project.gradingservice.exchange;

import lombok.Data;

@Data
public class StartQuestionRequest {
    private String email;
    private String roomId;
    private String assignmentId;
    private String questionId;
}
