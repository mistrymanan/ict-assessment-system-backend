package com.cdad.project.gradingservice.dto;

import lombok.Data;

@Data
public class BuildErrorResponse extends ErrorResponse{
    private String id;
    public BuildErrorResponse() {
        super();
    }
}