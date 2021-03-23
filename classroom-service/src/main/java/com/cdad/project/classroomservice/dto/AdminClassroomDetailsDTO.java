package com.cdad.project.classroomservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdminClassroomDetailsDTO {
    private String id;
    private String title;
    private String slug;
    private String ownerEmail;
    private String ownerName;
    private String ownerPicture;
    private Integer totalStudents;
    private Integer totalInstructors;
}