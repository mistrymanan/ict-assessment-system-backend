package com.cdad.project.userservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdminUserDetails {
    private String emailId;
    private String name;
    private String picture;
    private String userId;
    private Boolean isAdmin;
    private Boolean allowedClassroomCreation;
}
