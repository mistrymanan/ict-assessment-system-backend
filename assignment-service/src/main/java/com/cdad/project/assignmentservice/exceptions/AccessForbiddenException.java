package com.cdad.project.assignmentservice.exceptions;

import lombok.Data;

@Data
public class AccessForbiddenException extends Exception {
    public AccessForbiddenException(String message) {
        super(message);
    }
}
