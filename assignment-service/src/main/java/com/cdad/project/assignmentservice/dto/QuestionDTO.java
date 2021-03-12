package com.cdad.project.assignmentservice.dto;

import com.cdad.project.assignmentservice.entity.TestCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private UUID id;
    private String title;
    private String slug;
    private List<String> allowedLanguages;
    private Integer totalPoints;
    private boolean showExpectedOutput;
    private String description;
    private String solutionLanguage;
    private String solutionCode;
    private List<TestCase> testCases;
}
