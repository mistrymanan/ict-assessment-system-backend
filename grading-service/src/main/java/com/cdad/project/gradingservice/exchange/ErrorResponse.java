package com.cdad.project.gradingservice.exchange;

import com.cdad.project.gradingservice.entity.ResultStatus;
import com.cdad.project.gradingservice.entity.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String id;
    private String buildId;
    private String assignmentId;
    private String questionId;
    private Status status;
    private String message;
}