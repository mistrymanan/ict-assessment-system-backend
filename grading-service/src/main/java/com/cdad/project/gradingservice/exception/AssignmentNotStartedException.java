package com.cdad.project.gradingservice.exception;

import com.cdad.project.gradingservice.entity.Status;
import lombok.Data;

@Data
public class AssignmentNotStartedException extends Exception {
    private Status status = Status.ASSIGNMENT_NOT_STARTED;

    public AssignmentNotStartedException(String message) {
        super(message);
    }
}
