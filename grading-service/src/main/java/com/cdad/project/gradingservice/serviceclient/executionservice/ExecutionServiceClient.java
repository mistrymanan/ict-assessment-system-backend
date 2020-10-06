package com.cdad.project.gradingservice.serviceclient.executionservice;

import com.cdad.project.gradingservice.entity.Status;
import com.cdad.project.gradingservice.exception.RunCodeCompilationErrorException;
import com.cdad.project.gradingservice.serviceclient.executionservice.exceptions.BuildCompilationErrorException;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostBuildRequest;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostBuildResponse;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostRunRequest;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostRunResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class ExecutionServiceClient {
    //private final String BASE_URL = "http://35.184.28.10/api/executions";
    //private final String BASE_URL = "http://localhost:8081";
    private final String BASE_URL = "http://execution-service.default.svc.cluster.local:8080";
    private final WebClient webClient = WebClient.create(BASE_URL);
    private final String POST_RUN = "/run";
    private final String POST_BUILD = "/builds";
    private final ModelMapper modelMapper;

    public ExecutionServiceClient(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public PostRunResponse postRunCode(PostRunRequest request,String token) throws RunCodeCompilationErrorException {
        PostRunResponse response=webClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(POST_RUN)
                                .build()
                )
                .body(Mono.just(request),PostRunRequest.class)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(PostRunResponse.class).block();
        if(response.getStatus().equals(Status.COMPILE_ERROR)){
            throw new RunCodeCompilationErrorException(response.getMessage(),response.getStatus());
        }
            return response;
    }
    public PostBuildResponse postBuild(PostBuildRequest request,String token) throws BuildCompilationErrorException {
         PostBuildResponse postBuildResponse=webClient.post()
                .uri(uriBuilder -> uriBuilder
                .path(POST_BUILD)
                        .build()
                )
                .body(Mono.just(request),PostBuildRequest.class)
                 .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(PostBuildResponse.class).block();

            if(postBuildResponse.getStatus().equals(Status.COMPILE_ERROR)){
                throw new BuildCompilationErrorException(postBuildResponse.getMessage(), postBuildResponse.getId());
            }

        return postBuildResponse;
    }
}
