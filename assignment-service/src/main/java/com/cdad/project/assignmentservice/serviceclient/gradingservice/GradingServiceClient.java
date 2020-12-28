package com.cdad.project.assignmentservice.serviceclient.gradingservice;

import com.cdad.project.assignmentservice.serviceclient.gradingservice.dto.QuestionDTO;
import com.cdad.project.assignmentservice.serviceclient.gradingservice.dto.SubmissionDetailsDTO;
import com.cdad.project.assignmentservice.serviceclient.gradingservice.exeptions.SubmissionDetailsNotFoundException;
import com.cdad.project.assignmentservice.serviceclient.gradingservice.exeptions.SubmissionQuestionNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class GradingServiceClient {
  //private final String BASE_URL = "http://localhost:8082";
  private final String BASE_URL = "http://submission-service.default.svc.cluster.local:8080/public";
//  private final String BASE_URL = "http://35.184.28.10/public";
  private final WebClient webClient = WebClient.create(BASE_URL);
  private final String GET_SUBMISSION = "/submissions/{id}";
  private final String GET_QUESTION_OF_SUBMISSION = "/submissions/{assignmentId}/{questionId}";

  public Mono<SubmissionDetailsDTO> getSubmissionDetails(String assignmentId, Jwt jwt) {
    return webClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path(GET_SUBMISSION)
                    .build(assignmentId)
            )
            .headers(header -> header.setBearerAuth(jwt.getTokenValue()))
            .header("X-Secret", "top-secret-communication")
            .retrieve()
            .onStatus(
                    status -> status.equals(HttpStatus.NOT_FOUND),
                    response -> Mono.error(new SubmissionDetailsNotFoundException())
            )
            .bodyToMono(SubmissionDetailsDTO.class);
  }

  public Mono<QuestionDTO> getQuestionOfSubmission(String assignmentId, String questionId, Jwt jwt) {
    return webClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path(GET_QUESTION_OF_SUBMISSION)
                    .build(assignmentId, questionId)
            )
            .headers(header -> header.setBearerAuth(jwt.getTokenValue()))
            .header("X-Secret", "top-secret-communication")
            .retrieve()
            .onStatus(
                    status -> status.equals(HttpStatus.NOT_FOUND),
                    response -> Mono.error(new SubmissionQuestionNotFoundException())
            )
            .bodyToMono(QuestionDTO.class);
  }

}
