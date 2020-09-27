package com.cdad.project.assignmentservice.exchanges;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetQuestionRequest {
  @NotNull
  private String assignmentSlug;
  @NotNull
  private String questionSlug;
}
