package com.cdad.project.gradingservice.serviceclient.executionservice;

import com.cdad.project.gradingservice.entity.Status;
import com.cdad.project.gradingservice.exception.RunCodeCompilationErrorException;
import com.cdad.project.gradingservice.serviceclient.executionservice.exceptions.BuildCompilationErrorException;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Configuration
public class ExecutionServiceClient {
    //private final String BASE_URL = "http://35.184.28.10/api/executions";
    //private final String BASE_URL = "http://localhost:8081";
    private final String BASE_URL = "http://execution-service.default.svc.cluster.local:8080";
    private final WebClient webClient = WebClient.create(BASE_URL);
    private final String POST_RUN = "/run";
    private final String BUILDS = "/builds";
    private final ModelMapper modelMapper;

    public ExecutionServiceClient(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }


    public GetBuildsResponse getBuilds(GetBuildsRequest request, Jwt jwt) {
        Optional<GetBuildsResponse> getClassroomsResponse = webClient
                .method(HttpMethod.GET)
                .uri(uriBuilder ->
                        uriBuilder.path(BUILDS).build())
                .headers(httpHeaders -> {
                    httpHeaders.setBearerAuth(jwt.getTokenValue());
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                })
//                .header("X-Secret", "top-secret-communication")
                .body(Mono.just(request), GetBuildsRequest.class)
                .retrieve()
                .bodyToMono(GetBuildsResponse.class)
                .blockOptional();
        return getClassroomsResponse.get();
    }

    public PostRunResponse postRunCode(PostRunRequest request, String token) throws RunCodeCompilationErrorException {
        PostRunResponse response = webClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(POST_RUN)
                                .build()
                )
                .body(Mono.just(request), PostRunRequest.class)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(PostRunResponse.class).block();
        if (response.getStatus().equals(Status.COMPILE_ERROR)) {
            throw new RunCodeCompilationErrorException(response.getMessage(), response.getStatus());
        }
        return response;
    }

    public PostBuildResponse postBuild(PostBuildRequest request, String token) throws BuildCompilationErrorException {
        PostBuildResponse postBuildResponse = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(BUILDS)
                        .build()
                )
                .body(Mono.just(request), PostBuildRequest.class)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(PostBuildResponse.class).block();

        if (postBuildResponse.getStatus().equals(Status.COMPILE_ERROR)) {
            throw new BuildCompilationErrorException(postBuildResponse.getMessage(), postBuildResponse.getId());
        }

        return postBuildResponse;
    }
}
