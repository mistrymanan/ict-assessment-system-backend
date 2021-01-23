package com.cdad.project.userservice;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class UserServiceApplication {
  @Bean
  public ModelMapper modelMapper(){
    return new ModelMapper();
  }

  public static void main(String[] args) {
    SpringApplication.run(UserServiceApplication.class, args);
  }

//  @Bean
//  public RouteLocator routes(RouteLocatorBuilder builder) {
//    return builder.routes().build();
//  }
}
