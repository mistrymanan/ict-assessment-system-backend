package com.cdad.project.classroomservice.classroomservice.exceptions;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClassroomNotFound extends Exception {
    public ClassroomNotFound(String message) {
        super(message);
    }
}
