package com.cdad.project.assignmentservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
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
