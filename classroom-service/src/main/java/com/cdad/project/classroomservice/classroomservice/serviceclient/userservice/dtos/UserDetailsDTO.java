package com.cdad.project.classroomservice.classroomservice.serviceclient.userservice.dtos;

import java.util.HashSet;

public class UserDetailsDTO {
    String emailId;
    String name;
    String picture;
    HashSet<String> instructClassrooms;
    HashSet<String> enrolledClassrooms;
}