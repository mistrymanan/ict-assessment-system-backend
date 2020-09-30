package com.cdad.project.gradingservice.exchange;

import com.cdad.project.gradingservice.entity.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String id;
    private String buildId;
    private String assignmentId;
    private String questionId;
    private Status status;
    private String message;
}
