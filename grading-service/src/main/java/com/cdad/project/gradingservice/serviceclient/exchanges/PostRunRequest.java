package com.cdad.project.gradingservice.serviceclient.exchanges;

import com.cdad.project.gradingservice.entity.Language;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PostRunRequest {
    private String sourceCode;
    private String input;
    private Language language;
}