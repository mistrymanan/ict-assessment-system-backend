package com.cdad.project.classroomservice.serviceclient.userservice;

import com.cdad.project.classroomservice.exchanges.DeleteClassroomRequest;
import com.cdad.project.classroomservice.serviceclient.userservice.exchanges.AddInstructorsRequest;
import com.cdad.project.classroomservice.serviceclient.userservice.exchanges.EnrollUsersRequest;
import com.cdad.project.classroomservice.serviceclient.userservice.exchanges.RemoveInstructorsRequest;
import com.cdad.project.classroomservice.serviceclient.userservice.exchanges.UnrollUsersRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashSet;

@Configuration
public class UserServiceClient {
    private final String BASE_URL = "http://user-service.default.svc.cluster.local:8080";
    //private final String BASE_URL = "http://localhost:8081";
    private final WebClient webClient = WebClient.create(BASE_URL);
    private final String GET_USER_DETAILS = "/{id}/";
    private final String ENROLL="/enroll";
    private final String INSTRUCTORS="/instructors";

    public Void addInstructorToClass(String classroomSlug, HashSet<String> users, Jwt jwt){
        AddInstructorsRequest addInstructorsRequest=new AddInstructorsRequest(classroomSlug,users);
        System.out.println("Sending Req to User Service");
        return webClient.post()
                .uri(uriBuilder ->{
                    uriBuilder.path(INSTRUCTORS);
                    return uriBuilder.build();
                })
                .headers(httpHeaders -> {
                    httpHeaders.setBearerAuth(jwt.getTokenValue());
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                })
                .header("X-Secret", "top-secret-communication")
                //.header(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(addInstructorsRequest),AddInstructorsRequest.class)
                .retrieve()
                .bodyToMono(Void.class).block();
    }
    public Void removeInstructorFromClass(String classroomSlug,HashSet<String> users,Jwt jwt){
        RemoveInstructorsRequest removeInstructorsRequest=new RemoveInstructorsRequest(classroomSlug,users);
        return webClient.method(HttpMethod.DELETE)
                .uri(INSTRUCTORS)
                .headers(httpHeaders -> {
                    httpHeaders.setBearerAuth(jwt.getTokenValue());
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                })
                .header("X-Secret", "top-secret-communication")
                .body(Mono.just(removeInstructorsRequest), DeleteClassroomRequest.class)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
    public Void unrollUsersFromClass(String classroomSlug,HashSet<String> users,Jwt jwt){
        UnrollUsersRequest unrollUsersRequest=new UnrollUsersRequest(classroomSlug,users);
        return webClient.method(HttpMethod.DELETE)
                .uri(ENROLL)
                .headers(httpHeaders -> {
                    httpHeaders.setBearerAuth(jwt.getTokenValue());
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                })
                .header("X-Secret", "top-secret-communication")
                .body(Mono.just(unrollUsersRequest),UnrollUsersRequest.class)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
    public Void enrollUsersToClass(String classroomSlug,HashSet<String> users,Jwt jwt){
        EnrollUsersRequest enrollUsersRequest=new EnrollUsersRequest(classroomSlug,users);
        return webClient.post()
                .uri(ENROLL)
                .headers(httpHeaders -> {
                    httpHeaders.setBearerAuth(jwt.getTokenValue());
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                })
                .header("X-Secret", "top-secret-communication")
                //.header(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(enrollUsersRequest),EnrollUsersRequest.class)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}