package com.cdad.project.gradingservice.dto;

import com.cdad.project.gradingservice.entity.Status;
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