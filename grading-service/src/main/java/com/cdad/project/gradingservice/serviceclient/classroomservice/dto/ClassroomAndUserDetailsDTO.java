package com.cdad.project.gradingservice.serviceclient.classroomservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

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