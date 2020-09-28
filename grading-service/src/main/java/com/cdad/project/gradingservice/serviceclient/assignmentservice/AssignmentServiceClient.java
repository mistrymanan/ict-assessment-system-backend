package com.cdad.project.gradingservice.serviceclient.assignmentservice;

import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.Assignment;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.Question;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.exchanges.GetQuestionRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class AssignmentServiceClient {
  //private final String BASE_URL = "http://localhost:8082";
  private final String BASE_URL = "http://35.184.28.10/api/assignments";
  private final WebClient webClient = WebClient.create(BASE_URL);
  private final String GET_QUESTION = "/questions/id";
  private final String GET_ASSIGNMENT = "/id/{assignmentId}";

  public Mono<Question> getQuestion(GetQuestionRequest request) {
    return webClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path(GET_QUESTION)
                    .queryParam("assignmentId", request.getAssignmentId())
                    .queryParam("questionId", request.getQuestionId())
                    .build()
            )
            .retrieve()
            .bodyToMono(Question.class);
  }

  public Mono<Assignment> getAssignment(String assignmentId) {
    return webClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path(GET_ASSIGNMENT)
                    .build(assignmentId)
            )
            .retrieve()
            .bodyToMono(Assignment.class);
  }

}
