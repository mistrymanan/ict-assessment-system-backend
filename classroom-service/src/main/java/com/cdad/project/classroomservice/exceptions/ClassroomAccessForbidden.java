package com.cdad.project.classroomservice.exceptions;

import lombok.Data;

@Data
public class ClassroomAccessForbidden extends Exception {
    public ClassroomAccessForbidden(String message) {
        super(message);
    }
}
