package com.cdad.project.executionservice.dto;

import com.cdad.project.executionservice.dto.TestCase;
import com.cdad.project.executionservice.entity.Language;
import com.cdad.project.executionservice.entity.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Program {
    private String sourceCode;
    private String input;
    private String output;
    private List<TestCase> testCasesList;
    private Language language;
    private long executionTime;
    private long memoryUsage;
    private Status status;

    public Program() {
    }

    public Program(String sourceCode, String input,List<TestCase> testCasesList,Language language) {
        this.sourceCode = sourceCode;
        this.input = input;
        this.language=language;
        this.testCasesList=testCasesList;
    }
}