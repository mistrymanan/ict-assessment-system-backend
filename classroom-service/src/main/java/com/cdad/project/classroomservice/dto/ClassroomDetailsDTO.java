package com.cdad.project.classroomservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClassroomDetailsDTO {
    private String id;
    private String classroomSlug;
    private String classroomTitle;
    private String ownerEmail;
    private String ownerName;
}