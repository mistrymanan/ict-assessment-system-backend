package com.cdad.project.executionservice.dto;

import com.cdad.project.executionservice.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private Status status;
    private String message;
}