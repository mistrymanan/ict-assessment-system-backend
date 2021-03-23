package com.cdad.project.userservice.entity;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.HashSet;

@Document(collection = "users")
@Data
@ToString
public class User {
    @Id
    private String emailId;
    private String name;
    private String picture;
    private Boolean isAdmin;
    private Boolean allowedClassroomCreation;
    HashSet<String> instructClassrooms;
    HashSet<String> enrolledClassrooms;
}