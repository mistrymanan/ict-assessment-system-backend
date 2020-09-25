package com.cdad.project.executionservice.exchange;


import com.cdad.project.executionservice.dto.TestInput;
import com.cdad.project.executionservice.entity.Language;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.rmi.Naming;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PostBuildRequest {
    private String sourceCode;
    private List<TestInput> inputs;
    private Language language;
}
