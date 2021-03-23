package com.cdad.project.classroomservice.exceptions;

import lombok.Data;

@Data
public class NotAuthorizedForClassroomCreation extends Exception {
    public NotAuthorizedForClassroomCreation(String message) {
        super(message);
    }
}
