package com.cdad.project.assignmentservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.dekorate.kubernetes.annotation.KubernetesApplication;
import io.dekorate.kubernetes.annotation.Label;
import io.dekorate.kubernetes.annotation.Port;
import io.dekorate.kubernetes.annotation.ServiceType;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@KubernetesApplication(
        labels = @Label(key = "app", value = "assignment-service"),
        replicas = 2,
        expose = true,
        ports = @Port(name="http",containerPort = 8080),
        serviceType = ServiceType.NodePort
)
public class AssignmentServiceApplication {

  @Bean
  public ModelMapper modelMapper(){
    return new ModelMapper();
  }


  public static void main(String[] args) {
    SpringApplication.run(AssignmentServiceApplication.class, args);
  }

}
