package com.cdad.project.classroomservice.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.HashSet;

@Document(collection = "classrooms")
@Data
public class Classroom {
    @MongoId(FieldType.OBJECT_ID)
    private ObjectId id;
    private String title;
    @Indexed(unique = true)
    private String slug;
    @Indexed
    private String ownerEmail;
    private String ownerName;
    private HashSet<String> instructors;
    private HashSet<String> enrolledUsers;
}
