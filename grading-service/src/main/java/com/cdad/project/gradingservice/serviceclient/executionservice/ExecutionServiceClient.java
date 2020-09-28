package com.cdad.project.gradingservice.serviceclient.executionservice;

import com.cdad.project.gradingservice.entity.Status;
import com.cdad.project.gradingservice.serviceclient.executionservice.exceptions.BuildCompilationErrorException;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostBuildRequest;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostBuildResponse;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostRunRequest;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostRunResponse;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class ExecutionServiceClient {
    private final String BASE_URL = "http://35.184.28.10/api/executions";
    private final WebClient webClient = WebClient.create(BASE_URL);
    private final String POST_RUN = "/run";
    private final String POST_BUILD = "/builds";
    private final ModelMapper modelMapper;

    public ExecutionServiceClient(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

//    public static void main(String[] args) {
//        ExecutionServiceClient executionServiceClient=new ExecutionServiceClient();
//        PostRunRequest postRunRequest=new PostRunRequest();
//        postRunRequest.setSourceCode("#include <stdio.h> \n int main() {printf(\"Hello, World manan!\");return 0;}");
//        postRunRequest.setLanguage(Language.CPP);
//        Mono<PostRunResponse> postRunResponse=executionServiceClient.postRunCode(postRunRequest);
//        PostRunResponse  obj=postRunResponse.block();
//        System.out.println(obj.toString());
//    }

    public Mono<PostRunResponse> postRunCode(PostRunRequest request){
            return webClient.post()
                    .uri(uriBuilder ->
                            uriBuilder
                                    .path(POST_RUN)
                            .build()
                            )
                    .body(Mono.just(request),PostRunRequest.class)
                    .retrieve()
                    .bodyToMono(PostRunResponse.class);
    }
    public PostBuildResponse postBuild(PostBuildRequest request) throws BuildCompilationErrorException {
         PostBuildResponse postBuildResponse=webClient.post()
                .uri(uriBuilder -> uriBuilder
                .path(POST_BUILD)
                        .build()
                )
                .body(Mono.just(request),PostBuildRequest.class)
                .retrieve()
                .bodyToMono(PostBuildResponse.class).block();

            if(postBuildResponse.getStatus().equals(Status.COMPILE_ERROR)){
                throw modelMapper.map(postBuildResponse, BuildCompilationErrorException.class);
            }

        return postBuildResponse;
    }
}
