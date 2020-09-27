package com.cdad.project.gradingservice;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class GradingServiceApplication {
  @Bean
  public ModelMapper modelMapper(){
    return new ModelMapper();
  }
  public static void main(String[] args) {
    SpringApplication.run(GradingServiceApplication.class, args);
  }

}
