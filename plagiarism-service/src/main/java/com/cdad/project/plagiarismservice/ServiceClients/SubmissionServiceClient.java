package com.cdad.project.plagiarismservice.ServiceClients;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SubmissionServiceClient {
    private final String BASE_URL = "http://aas.ict.gnu.ac.in/api/v2/submissions/";
    //    private final String BASE_URL = "http://classroom-service.default.svc.cluster.local:8080";
    private final WebClient webClient = WebClient.create(BASE_URL);
    private final String GET_PUBLIC_PLAGIARISM = "public-plagiarism/{assignmentId}/{questionId}";

    public List<UserQuestionResponseDTO> getSubmittedCodes(String assignmentId, String questionId, Jwt jwt){
        Map<String, String> pathVariable = new HashMap<>();
        pathVariable.put("assignmentId", assignmentId);
        pathVariable.put("questionId",questionId);
        UserQuestionResponseDTO[] userQuestionResponseDTOS=webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_PUBLIC_PLAGIARISM)
                        .build(pathVariable)
                )
                .headers(httpHeaders -> httpHeaders.setBearerAuth(jwt.getTokenValue()))
                //.header("X-Secret", "top-secret-communication")
                .retrieve()
                .bodyToMono(UserQuestionResponseDTO[].class)
                .block();
        return Arrays.asList(userQuestionResponseDTOS);
    }

}
