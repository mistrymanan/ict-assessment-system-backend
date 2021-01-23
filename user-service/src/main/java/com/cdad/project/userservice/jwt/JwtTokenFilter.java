//package com.cdad.project.userservice.jwt;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Objects;
//
//public class JwtTokenFilter extends OncePerRequestFilter {
//  private final JwtTokenProvider jwtTokenProvider;
//
//  public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
//    this.jwtTokenProvider = jwtTokenProvider;
//  }
//
//  @Override
//  protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
//    String token = jwtTokenProvider.resolveToken(httpServletRequest);
//    if (Objects.nonNull(token) && jwtTokenProvider.validateToken(token)) {
//      Authentication auth = jwtTokenProvider.getAuthentication(token);
//      if (Objects.nonNull(auth)) {
//        SecurityContextHolder.getContext().setAuthentication(auth);
//      }
//    }
//    filterChain.doFilter(httpServletRequest, httpServletResponse);
//  }
//}
