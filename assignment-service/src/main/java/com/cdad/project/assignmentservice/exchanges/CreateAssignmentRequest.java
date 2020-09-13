package com.cdad.project.assignmentservice.exchanges;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@ToString
public class CreateAssignmentRequest {
  @NotNull
  private String title;
  @NotNull
  private boolean isTimed;
  private Integer duration;
  @NotNull
  private boolean hasStartTime;
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime startTime;
  @NotNull
  private boolean hasDeadline;
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime deadline;
}
