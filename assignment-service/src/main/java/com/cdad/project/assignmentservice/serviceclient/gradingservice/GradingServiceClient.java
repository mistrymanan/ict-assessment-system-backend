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

import java.util.HashMap;

@Configuration
public class GradingServiceClient {
    //private final String BASE_URL = "http://localhost:8082";
    private final String BASE_URL = "http://submission-service-v2.default.svc.cluster.local:8080/";
    //  private final String BASE_URL = "http://35.184.28.10/public";
    private final WebClient webClient = WebClient.create(BASE_URL);
    private final String GET_SUBMISSION = "{classroomSlug}/public/submissions/{assignmentId}";
    private final String GET_QUESTION_OF_SUBMISSION = "{classroomSlug}/public/submissions/{assignmentId}/{questionId}";

    public Mono<SubmissionDetailsDTO> getSubmissionDetails(String assignmentId, String classroomSlug, Jwt jwt) {
        HashMap<String, String> pathVariable = new HashMap<>();
        pathVariable.put("classroomSlug", classroomSlug);
        pathVariable.put("assignmentId", assignmentId);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_SUBMISSION)
                        .build(pathVariable)
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

    public Mono<QuestionDTO> getQuestionOfSubmission(String assignmentId, String questionId, String classroomSlug, Jwt jwt) {
        HashMap<String, String> pathVariable = new HashMap<>();
        pathVariable.put("classroomSlug", classroomSlug);
        pathVariable.put("assignmentId", assignmentId);
        pathVariable.put("questionId", questionId);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_QUESTION_OF_SUBMISSION)
                        .build(pathVariable)
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
