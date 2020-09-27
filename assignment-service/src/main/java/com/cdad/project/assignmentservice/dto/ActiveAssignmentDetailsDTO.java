package com.cdad.project.assignmentservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActiveAssignmentDetailsDTO {
  private String id;
  private String title;
  private String slug;
  private String currentStatus;
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
