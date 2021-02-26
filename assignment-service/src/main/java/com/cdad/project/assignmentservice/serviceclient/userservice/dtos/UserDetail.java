package com.cdad.project.assignmentservice.serviceclient.userservice.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDetail {
    private String emailId;
    private String name;
    private String picture;
}