package com.cdad.project.gradingservice.serviceclient.executionservice.exceptions;

import com.cdad.project.gradingservice.entity.Language;
import com.cdad.project.gradingservice.entity.Status;
import com.cdad.project.gradingservice.serviceclient.executionservice.exceptions.CompilationErrorException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuildCompilationErrorException extends CompilationErrorException {
    private String buildId;
    public BuildCompilationErrorException(String message,String buildId) {
        super(message);
        this.buildId=buildId;
    }
}