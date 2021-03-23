package com.cdad.project.userservice.entity;

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
    private String picture;
    private Boolean isAdmin;
    private Boolean allowedClassroomCreation;
    public static CurrentUser fromJwt(Jwt jwt) {
        String name = jwt.getClaimAsString("name");
        String email = jwt.getClaimAsString("email");
        String picture = jwt.getClaimAsString("picture");
        Boolean isAdmin= jwt.getClaimAsBoolean("isAdmin");
        Boolean hasCreateClassroomRights= jwt.getClaimAsBoolean("hasCreateClassroomRights");
        return new CurrentUser(name, email, picture,isAdmin,hasCreateClassroomRights);
    }
}