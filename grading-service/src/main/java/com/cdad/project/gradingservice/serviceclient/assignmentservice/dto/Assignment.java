package com.cdad.project.gradingservice.serviceclient.assignmentservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jdk.jfr.DataAmount;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
public class Assignment {
  private String id;
  private String title;
  private String slug;
  private String status;
  private boolean timed;
  private Integer duration;
  private boolean hasStartTime;
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime startTime;
  private boolean hasDeadline;
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime deadline;
  List<QuestionDetails> questions;
}