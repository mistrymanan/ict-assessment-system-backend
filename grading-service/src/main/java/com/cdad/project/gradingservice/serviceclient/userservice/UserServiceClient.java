package com.cdad.project.gradingservice.serviceclient.userservice;

import com.cdad.project.gradingservice.entity.CurrentUser;
import com.cdad.project.gradingservice.serviceclient.userservice.dtos.UserDetailsDTO;
import com.cdad.project.gradingservice.serviceclient.userservice.exceptions.UserNotFoundException;
import com.cdad.project.gradingservice.serviceclient.userservice.exchanges.AddInstructorsRequest;
import com.cdad.project.gradingservice.serviceclient.userservice.exchanges.GetUsersDetailRequest;
import com.cdad.project.gradingservice.serviceclient.userservice.exchanges.GetUsersDetailsResponse;
import com.cdad.project.gradingservice.serviceclient.userservice.exchanges.RemoveInstructorsRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Optional;

@Configuration
public class UserServiceClient {

    private final String BASE_URL = "http://user-service.default.svc.cluster.local:8080";
    //private final String BASE_URL = "http://ict.assessment-system.tech:80/api/users";
    //private final String BASE_URL = "http://localhost:8081";

    private final WebClient webClient = WebClient.create(BASE_URL);
    private final String GET_USER_DETAILS = "/{email}/";
    private final String ENROLL = "/enroll";
    private final String INSTRUCTORS = "/instructors";

    public GetUsersDetailsResponse getUsersDetails(GetUsersDetailRequest request, Jwt jwt) {
        Optional<GetUsersDetailsResponse> getClassroomsResponse = webClient
                .method(HttpMethod.GET)
                .uri(UriBuilder::build)
                .headers(httpHeaders -> {
                    httpHeaders.setBearerAuth(jwt.getTokenValue());
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                })
                .header("X-Secret", "top-secret-communication")
                .body(Mono.just(request), GetUsersDetailRequest.class)
                .retrieve()
                .bodyToMono(GetUsersDetailsResponse.class)
                .blockOptional();
        return getClassroomsResponse.get();
    }

}