package com.cdad.project.assignmentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDetails {
    private UUID id;
    private String title;
    private String slug;
    private List<String> allowedLanguages;
    private Integer totalPoints;
}
