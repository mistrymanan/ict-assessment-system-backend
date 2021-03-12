package com.cdad.project.executionservice.dto;

import com.cdad.project.executionservice.entity.Language;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProgramInput {
    private String sourceCode;
    private Language language;

    public ProgramInput() {
    }

    public ProgramInput(String sourceCode, Language language) {
        this.sourceCode = sourceCode;
        this.language = language;
    }
}