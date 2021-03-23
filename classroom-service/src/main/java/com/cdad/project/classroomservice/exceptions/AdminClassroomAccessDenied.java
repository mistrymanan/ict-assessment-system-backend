package com.cdad.project.classroomservice.exceptions;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdminClassroomAccessDenied extends Exception {
    public AdminClassroomAccessDenied(String message) {
        super(message);
    }
}
