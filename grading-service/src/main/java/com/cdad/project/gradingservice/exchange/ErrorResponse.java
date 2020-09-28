package com.cdad.project.gradingservice.exchange;

import com.cdad.project.gradingservice.entity.Status;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ErrorResponse {
    private String id;
    private String buildId;
    private String assignmentId;
    private String questionId;
    private Status status;
    private String message;
}
