package com.cdad.project.classroomservice.classroomservice.serviceclient.userservice;

import org.springframework.web.reactive.function.client.WebClient;

public class UserServiceClient {
    //private final String BASE_URL = "http://user-service.default.svc.cluster.local:8080/public";
    private final String BASE_URL = "http://localhost:8081";
    private final WebClient webClient = WebClient.create(BASE_URL);
    private final String GET_USER_DETAILS = "/{id}/";
}
