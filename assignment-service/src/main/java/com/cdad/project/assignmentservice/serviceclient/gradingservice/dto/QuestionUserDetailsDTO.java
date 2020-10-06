package com.cdad.project.assignmentservice.serviceclient.gradingservice.dto;

import com.cdad.project.assignmentservice.serviceclient.gradingservice.enums.QuestionStatus;
import com.cdad.project.assignmentservice.serviceclient.gradingservice.enums.ResultStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuestionUserDetailsDTO {
  private String questionId;
  private String buildId;
  private String title;
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime time;
  private QuestionStatus questionStatus;
  private ResultStatus resultStatus;
  private Double totalPoints;
  private Double score;
}