package com.cdad.project.gradingservice.serviceclient.executionservice;

import com.cdad.project.gradingservice.entity.Language;
import com.cdad.project.gradingservice.exchange.PostRunCodeResponse;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostRunRequest;
import com.cdad.project.gradingservice.serviceclient.executionservice.exchanges.PostRunResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Configuration
public class ExecutionServiceClient {
    private final String BASE_URL = "http://localhost:8081";
    private final WebClient webClient = WebClient.create(BASE_URL);
    private final String POST_RUN = "/run";
    private final String POST_BUILD_CODE = "/builds";

    public static void main(String[] args) {
        ExecutionServiceClient executionServiceClient=new ExecutionServiceClient();
        PostRunRequest postRunRequest=new PostRunRequest();
        postRunRequest.setSourceCode("#include <stdio.h> \n int main() {printf(\"Hello, World manan!\");return 0;}");
        postRunRequest.setLanguage(Language.CPP);
        Mono<PostRunResponse> postRunResponse=executionServiceClient.postRunCode(postRunRequest);
        PostRunResponse  obj=postRunResponse.block();
        System.out.println(obj.toString());
    }

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
}
