package com.cdad.project.gradingservice.serviceclient.assignmentservice.exchanges;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GetQuestionRequest {
  @NotNull
  private String assignmentId;
  @NotNull
  private String questionId;
}
