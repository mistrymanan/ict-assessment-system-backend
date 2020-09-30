package com.cdad.project.gradingservice.serviceclient.executionservice.exchanges;

import com.cdad.project.gradingservice.entity.Status;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
public class PostRunResponse {
private String output;
private Status status;
private String message;
private long executionTime;
}
