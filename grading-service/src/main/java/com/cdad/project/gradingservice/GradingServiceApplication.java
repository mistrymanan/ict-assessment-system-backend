package com.cdad.project.gradingservice;

import com.cdad.project.gradingservice.entity.Language;
import org.modelmapper.ModelMapper;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.AssignmentServiceClient;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.Assignment;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.Question;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.exchanges.GetQuestionRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@SpringBootApplication
@Log4j2
public class GradingServiceApplication {

  @Bean
  public ModelMapper modelMapper(){
    return new ModelMapper();
  }
  private final AssignmentServiceClient assignmentServiceClient;

  public GradingServiceApplication(AssignmentServiceClient assignmentServiceClient) {
    this.assignmentServiceClient = assignmentServiceClient;
  }
//  public HashMap<Language,String> languageMap(){
//    HashMap<Language,String> hashMap=new HashMap<>();
//    hashMap.put(Language.C,"C");
//    hashMap.put(Language.CPP,"C++");
//    hashMap.put(Language.JAVA,"Java");
//    hashMap.put(Language.PYTHON,"Python3.8");
//  }

  public static void main(String[] args) {
    SpringApplication.run(GradingServiceApplication.class, args);
  }

}
