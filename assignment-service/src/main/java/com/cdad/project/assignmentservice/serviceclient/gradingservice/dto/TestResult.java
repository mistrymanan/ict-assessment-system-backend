package com.cdad.project.assignmentservice.serviceclient.gradingservice.dto;

import com.cdad.project.assignmentservice.serviceclient.gradingservice.enums.Reason;
import com.cdad.project.assignmentservice.serviceclient.gradingservice.enums.ResultStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestResult {
  private String id;
  private ResultStatus status;
  private Reason reason;
}
