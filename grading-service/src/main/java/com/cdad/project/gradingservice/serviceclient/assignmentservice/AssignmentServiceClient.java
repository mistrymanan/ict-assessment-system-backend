package com.cdad.project.gradingservice.serviceclient.assignmentservice;

import com.cdad.project.gradingservice.exception.AccessForbiddenException;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.Assignment;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.Question;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.exchanges.GetQuestionRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class AssignmentServiceClient {
  //private final String BASE_URL = "http://localhost:8082";
  private final String BASE_URL = "http://35.184.28.10/api/assignments";
  private final WebClient webClient = WebClient.create(BASE_URL);
  private final String GET_QUESTION = "/questions/id";
  private final String GET_ASSIGNMENT = "/id/{assignmentId}";

  public Mono<Question> getQuestion(GetQuestionRequest request,String token) {
    return webClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path(GET_QUESTION)
                    .queryParam("assignmentId", request.getAssignmentId())
                    .queryParam("questionId", request.getQuestionId())
                    .build()

            )
            .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
            .retrieve()
            .bodyToMono(Question.class);
  }

  public Mono<Assignment> getAssignment(String assignmentId,String token) {
    return webClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path(GET_ASSIGNMENT)
                    .build(assignmentId)
            )
            .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError,clientResponse ->
                    Mono.error(new AccessForbiddenException("Forbidden")))
            .bodyToMono(Assignment.class);
  }

}
