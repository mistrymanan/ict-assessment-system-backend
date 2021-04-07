package com.cdad.project.gradingservice.serviceclient.classroomservice;

import com.cdad.project.gradingservice.serviceclient.assignmentservice.dto.Question;
import com.cdad.project.gradingservice.serviceclient.classroomservice.dto.ClassroomAndUserDetailsDTO;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClassroomServiceClient {

    private final String BASE_URL = "http://aas.ict.gnu.ac.in/api/classrooms";
//    private final String BASE_URL = "http://classroom-service.default.svc.cluster.local:8080";
    private final WebClient webClient = WebClient.create(BASE_URL);
    private final String GET_CLASSROOM_DETAILS = "{classroomSlug}";

    public ClassroomAndUserDetailsDTO getClassroomDetails(String classroomSlug, String token){
        Map<String, String> pathVariable = new HashMap<>();
        pathVariable.put("classroomSlug", classroomSlug);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_CLASSROOM_DETAILS)
                        .build(pathVariable)
                )
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                //.header("X-Secret", "top-secret-communication")
                .retrieve()
                .bodyToMono(ClassroomAndUserDetailsDTO.class)
                .block();
    }

}
