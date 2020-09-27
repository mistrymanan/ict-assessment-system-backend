package com.cdad.project.assignmentservice.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserQuestionDTO {
  private UUID id;
  private String title;
  private String slug;
  private List<String> allowedLanguages;
  private int totalPoints;
  private String description;
  private float currentScore;
  private String currentStatus;
  private boolean showExpectedOutput;
}
