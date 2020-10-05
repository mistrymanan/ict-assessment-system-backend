package com.cdad.project.userservice.jwt;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

public class JwtTokenProvider {
  public String resolveToken(HttpServletRequest httpServletRequest) {
    String bearerToken = httpServletRequest.getHeader("Authorization");
    return Objects.nonNull(bearerToken) && bearerToken.startsWith("Bearer") ? bearerToken.substring(7) : null;
  }

  public boolean validateToken(String token) {
    return false;
  }

  public Authentication getAuthentication(String token) {
    return null;
  }
}
