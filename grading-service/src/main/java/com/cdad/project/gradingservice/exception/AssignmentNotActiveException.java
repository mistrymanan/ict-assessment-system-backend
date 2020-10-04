package com.cdad.project.gradingservice.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AssignmentNotActiveException extends Exception {
    public AssignmentNotActiveException(String message) {
        super(message);
    }
}
