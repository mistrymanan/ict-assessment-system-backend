package com.cdad.project.assignmentservice.dto;

import com.cdad.project.assignmentservice.entity.Question;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AssignmentDTO {
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
