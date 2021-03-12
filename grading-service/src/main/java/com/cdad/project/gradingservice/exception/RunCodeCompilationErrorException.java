package com.cdad.project.gradingservice.exception;

import com.cdad.project.gradingservice.entity.Status;
import lombok.Data;

@Data
public class RunCodeCompilationErrorException extends Exception {
    private Status status;

    public RunCodeCompilationErrorException(String message, Status status) {
        super(message);
        this.status = status;
    }
}
