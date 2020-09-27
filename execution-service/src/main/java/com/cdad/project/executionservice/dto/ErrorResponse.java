package com.cdad.project.executionservice.dto;

import com.cdad.project.executionservice.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private Status status;
    private String message;
}