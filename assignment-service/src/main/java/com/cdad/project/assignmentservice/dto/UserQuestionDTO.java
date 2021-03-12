package com.cdad.project.assignmentservice.dto;

import com.cdad.project.assignmentservice.serviceclient.gradingservice.enums.QuestionStatus;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Data
@ToString
public class UserQuestionDTO {
    private UUID id;
    private String title;
    private String slug;
    private List<String> allowedLanguages;
    private int totalPoints;
    private String description;
    private Double currentScore;
    private QuestionStatus currentStatus;
    private boolean showExpectedOutput;
}
