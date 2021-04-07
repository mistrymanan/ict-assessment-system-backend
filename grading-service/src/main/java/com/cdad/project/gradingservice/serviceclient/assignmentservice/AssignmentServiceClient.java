package com.cdad.project.gradingservice.serviceclient.assignmentservice;

import com.cdad.project.gradingservice.exception.AccessForbiddenException;
import com.cdad.project.gradingservice.exception.AssignmentNotFound;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.Assignment;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.Question;
import com.cdad.project.gradingservice.serviceclient.assignmentservice.exchanges.GetQuestionRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AssignmentServiceClient {
    //private final String BASE_URL = "http://localhost:8082";
        private final String BASE_URL = "http://aas.ict.gnu.ac.in/api/v2/assignments";
//    private final String BASE_URL = "http://assignment-service-v2.default.svc.cluster.local:8080";
    private final WebClient webClient = WebClient.create(BASE_URL);
    private final String GET_QUESTION = "{classroomSlug}/public/questions/id";
    private final String GET_ASSIGNMENT = "{classroomSlug}/public/assignments/{assignmentId}";
    private final String GET_USER_ASSIGNMENT = "{classroomSlug}/id/{assignmentId}";

    public Mono<Question> getQuestion(GetQuestionRequest request, String classroomSlug, String token) {
        //System.out.println(request);
        Map<String, String> pathVariable = new HashMap<>();
        pathVariable.put("classroomSlug", classroomSlug);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_QUESTION)
                        .queryParam("assignmentId", request.getAssignmentId())
                        .queryParam("questionId", request.getQuestionId())
                        .build(pathVariable)
                )
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .header("X-Secret", "top-secret-communication")
                .retrieve()
                .bodyToMono(Question.class);
    }

    public Mono<Assignment> getUserAssignment(String assignmentId, String classroomSlug, String token) {
        Map<String, String> pathVariable = new HashMap<>();
        pathVariable.put("classroomSlug", classroomSlug);
        pathVariable.put("assignmentId", assignmentId);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_USER_ASSIGNMENT)
                        .build(pathVariable)
                )
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse ->
                        Mono.error(new AccessForbiddenException("Forbidden")))
                .bodyToMono(Assignment.class);
    }

    public Mono<Assignment> getAssignment(String assignmentId, String classroomSlug, String token) {
        Map<String, String> pathVariable = new HashMap<>();
        pathVariable.put("classroomSlug", classroomSlug);
        pathVariable.put("assignmentId", assignmentId);
        Assignment assignment = new Assignment();
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_ASSIGNMENT)
                        .build(pathVariable)
                )
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .header("X-Secret", "top-secret-communication")
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse ->
                        Mono.error(new AssignmentNotFound("Not Found")))
                .bodyToMono(Assignment.class);
    }
}
