package com.cdad.project.executionservice.entity;

import lombok.*;

@Data
@AllArgsConstructor
public class Program {
    private String sourceCode;
    private String input;
    private Language language;
    private Integer executionTime;
    private long memoryUsage;

    public Program() {
    }

    public Program(String sourceCode, String input,Language language) {
        this.sourceCode = sourceCode;
        this.input = input;
        this.language=language;
    }

}