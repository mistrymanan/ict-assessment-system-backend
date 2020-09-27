package com.cdad.project.executionservice.exceptions;

import com.cdad.project.executionservice.dto.ProgramInput;
import com.cdad.project.executionservice.entity.Language;
import com.cdad.project.executionservice.entity.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuildCompilationErrorException extends CompilationErrorException {
    private String id;
    private Status status;
    private String sourceCode;
    private Language language;
    public BuildCompilationErrorException(String message) {
        super(message);
    }
}
