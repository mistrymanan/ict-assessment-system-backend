package com.cdad.project.userservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDetails {
    private String emailId;
    private String name;
    private String picture;
}
