package com.cdad.project.userservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
//  public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
//    this.jwtTokenProvider = jwtTokenProvider;
//  }

  @Override
  protected AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManagerBean();
  }
  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http
            .cors()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/actuator/**").permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .oauth2ResourceServer()
            .jwt();
  }
//  private final JwtTokenProvider jwtTokenProvider;

//  @Override
//  protected void configure(HttpSecurity http) throws Exception {
//    http
//            .csrf().disable()
//            .formLogin().disable()
//            .httpBasic().disable()
//            .authorizeRequests()
//            .antMatchers("/login").permitAll()
//            .anyRequest().authenticated();
////    JwtTokenFilter authFilter = new JwtTokenFilter(jwtTokenProvider);
////    http
////            .formLogin().disable()
////            .httpBasic().disable()
////            .csrf().disable()
////            .sessionManagement().sessionCreationPolicy((SessionCreationPolicy.STATELESS))
////            .and()
////            .exceptionHandling()
////            .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
////            .and()
////            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
//
//
//  }
}
