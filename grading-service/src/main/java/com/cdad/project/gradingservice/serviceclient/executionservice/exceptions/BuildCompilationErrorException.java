package com.cdad.project.gradingservice.serviceclient.executionservice.exceptions;

import com.cdad.project.gradingservice.entity.Language;
import com.cdad.project.gradingservice.entity.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuildCompilationErrorException extends CompilationErrorException {
    private String id;
    private Status status;
    private String sourceCode;
    private Language language;
    public BuildCompilationErrorException(String message,String id) {
        super(message);
        this.id=id;
    }
}