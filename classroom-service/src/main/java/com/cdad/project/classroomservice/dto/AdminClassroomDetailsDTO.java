package com.cdad.project.classroomservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

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
