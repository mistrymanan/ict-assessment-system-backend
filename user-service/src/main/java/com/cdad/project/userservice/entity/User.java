package com.cdad.project.userservice.entity;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.HashSet;

@Document(collection = "users")
@Data
@ToString
public class User {
    @MongoId
    String emailId;
    String name;
    String picture;
    HashSet<String> instructClassrooms;
    HashSet<String> enrolledClassrooms;
}