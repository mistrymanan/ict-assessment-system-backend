package com.cdad.project.classroomservice.serviceclient.notificationservice;

import com.cdad.project.classroomservice.serviceclient.notificationservice.exchanges.PostEmailNotification;
import com.cdad.project.classroomservice.serviceclient.userservice.exchanges.AddInstructorsRequest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;

@Service
public class NotificationServiceClient {
    private final String BASE_URL = "http://notification-service.default.svc.cluster.local:8080";
    //private final String BASE_URL = "http://ict.assessment-system.tech:80/api/users";
    //private final String BASE_URL = "http://localhost:8081";

    private final WebClient webClient = WebClient.create(BASE_URL);
    //private final String URL = "/";
    public Void sendEmailNotification(HashSet<String> emails, String subject, String message) {
        PostEmailNotification request=new PostEmailNotification(emails,subject,message);
        return webClient.post()
                .uri(UriBuilder::build)
                .headers(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                })
                //.header("X-Secret", "top-secret-communication")
                //.header(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(request), PostEmailNotification.class)
                .retrieve()
                .bodyToMono(Void.class).block();
    }
}