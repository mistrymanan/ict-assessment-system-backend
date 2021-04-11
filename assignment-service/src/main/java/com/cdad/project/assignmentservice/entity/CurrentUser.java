package com.cdad.project.assignmentservice.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.security.oauth2.jwt.Jwt;

@Data
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrentUser {
    private String name;
    private String email;
    private Boolean isAdmin;
    public static CurrentUser fromJwt(Jwt jwt) {
        String name = jwt.getClaimAsString("name");
        String email = jwt.getClaimAsString("email");
        Boolean isAdmin=jwt.getClaimAsBoolean("isAdmin");
        return new CurrentUser(name, email,isAdmin);
    }
}
