package com.cdad.project.assignmentservice.exceptions;

import lombok.Data;

@Data
public class AssignmentAlreadyExistsException extends Exception {
    public AssignmentAlreadyExistsException(String message) {
        super(message);
    }
}
