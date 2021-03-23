package com.cdad.project.classroomservice.dto;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.cdad.project.classroomservice.serviceclient.userservice.dtos.UserDetail;

import java.util.List;

@Data
@NoArgsConstructor
public class ClassroomAndUserDetailsDTO {
    private String title;
    private String slug;
    private String ownerEmail;
    private String ownerName;
    private List<UserDetail> instructors;
    private List<UserDetail> enrolledUsers;
}