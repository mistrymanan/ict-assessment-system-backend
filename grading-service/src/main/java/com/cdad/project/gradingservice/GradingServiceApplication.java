package com.cdad.project.gradingservice;

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

@SpringBootApplication
@Log4j2
public class GradingServiceApplication implements CommandLineRunner {

  @Bean
  public ModelMapper modelMapper(){
    return new ModelMapper();
  }
  private final AssignmentServiceClient assignmentServiceClient;

  public GradingServiceApplication(AssignmentServiceClient assignmentServiceClient) {
    this.assignmentServiceClient = assignmentServiceClient;
  }

  public static void main(String[] args) {
    SpringApplication.run(GradingServiceApplication.class, args);
  }


  @Override
  public void run(String... args) throws Exception {
//    GetQuestionRequest request = new GetQuestionRequest();
//    request.setAssignmentId("5f67a3d05b628f6da2b4f8cd");
//    request.setQuestionId("b59d2ae0-856d-466e-b537-7dec6d531268");
//    Mono<Question> questionMono = this.assignmentServiceClient.getQuestion(request);
//    Question question = questionMono.block();
//    log.info(question);
//    Assignment assignment = this.assignmentServiceClient
//            .getAssignment("5f67a3d05b628f6da2b4f8cd").block();
//    log.info(assignment);
  }
}
