package com.cdad.project.assignmentservice.dto;

import com.cdad.project.assignmentservice.serviceclient.gradingservice.enums.SubmissionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActiveAssignmentDetailsDTO {
  private String id;
  private String title;
  private String slug;
  private SubmissionStatus submissionStatus;
  private String status;
  private Integer totalPoints;
  private Double currentScore;
  private boolean timed;
  private Integer duration;
  private boolean hasStartTime;
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime startTime;
  private boolean hasDeadline;
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime deadline;
  List<UserQuestionDTO> questions;
}
