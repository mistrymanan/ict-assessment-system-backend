package com.cdad.project.executionservice.dto;

import com.cdad.project.executionservice.entity.Status;
import lombok.*;

@Data
public class BuildErrorResponse extends ErrorResponse{
    private String id;
    public BuildErrorResponse() {
        super();
    }
}
