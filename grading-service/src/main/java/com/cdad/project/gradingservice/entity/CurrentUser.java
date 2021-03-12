package com.cdad.project.gradingservice.entity;

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

    public static CurrentUser fromJwt(Jwt jwt) {
        String name = jwt.getClaimAsString("name");
        String email = jwt.getClaimAsString("email");
        return new CurrentUser(name, email);
    }
}
