package com.cdad.project.gradingservice.exception;

import com.cdad.project.gradingservice.entity.Status;
import lombok.Data;

@Data
public class RunCodeCompilationError extends Exception {
    private Status status;

    public RunCodeCompilationError(String message,Status status) {
        super(message);
        this.status=status;
    }
}
