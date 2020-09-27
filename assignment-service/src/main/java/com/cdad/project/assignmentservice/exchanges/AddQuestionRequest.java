package com.cdad.project.assignmentservice.exchanges;

import com.cdad.project.assignmentservice.entity.TestCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AddQuestionRequest {
  private String assignmentId;
  private String title;
  private List<String> allowedLanguages;
  private Integer totalPoints;
  private boolean showExpectedOutput;
  private String description;
  private String solutionLanguage;
  private String solutionCode;
  private List<TestCase> testCases;
}
