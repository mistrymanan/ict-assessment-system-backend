package com.cdad.project.assignmentservice.serviceclient.gradingservice.dto;

import com.cdad.project.assignmentservice.serviceclient.gradingservice.enums.SubmissionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class SubmissionDetailsDTO {
  private UUID id;
  private String email;
  private String assignmentId;
  private SubmissionStatus submissionStatus;
  private List<QuestionUserDetailsDTO> questionEntities;
  private Double currentScore;
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime startOn;
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime completedOn;
  private Double assignmentScore;
}