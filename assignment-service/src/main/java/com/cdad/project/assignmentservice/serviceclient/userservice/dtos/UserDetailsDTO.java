package com.cdad.project.assignmentservice.serviceclient.userservice.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;

@Data
@NoArgsConstructor
public class UserDetailsDTO {
    String emailId;
    String name;
    String picture;
    HashSet<String> instructClassrooms;
    HashSet<String> enrolledClassrooms;
}