package com.cdad.project.gradingservice.serviceclient.classroomservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDetail {
    private String emailId;
    private String name;
    private String picture;
}