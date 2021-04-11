package com.cdad.project.plagiarismservice.ServiceClients.mossapi;

import com.cdad.project.plagiarismservice.ServiceClients.UserQuestionResponseDTO;
import com.cdad.project.plagiarismservice.ServiceClients.mossapi.dto.GetProcessedData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;

import static org.springframework.http.MediaType.TEXT_HTML;

@Service
public class GraphDataProducerServiceClient {

    final private String BASE_URL="http://moss-api-service.default.svc.cluster.local:8080/";
    final private String GRAPH_PARSER="parse-graph";
//    final private WebClient webClient= WebClient.create(BASE_URL);

    public GetProcessedData fetchDataForGraph(String url){

//        return webClient.get()
//                .uri(uriBuilder -> uriBuilder
//                        .path(GRAPH_PARSER)
//                        .queryParam("url",url)
//                        .build()
//                )
//                .retrieve()
//                .bodyToMono(GetProcessedData.class)
//                .block();
        return WebClient.builder().baseUrl(BASE_URL).exchangeStrategies(
                ExchangeStrategies.builder().codecs(this::acceptedCodecs).build()
        ).build()
                .get()
                .uri(uriBuilder -> {
                    return uriBuilder
                            .path(GRAPH_PARSER)
                            .queryParam("url",url)
                            .build();
                })
                .retrieve()
                .bodyToMono(GetProcessedData.class)
                .block();
    }
    private void acceptedCodecs(ClientCodecConfigurer clientCodecConfigurer) {
        clientCodecConfigurer.customCodecs().encoder(new Jackson2JsonEncoder(new ObjectMapper(), TEXT_HTML));
        clientCodecConfigurer.customCodecs().decoder(new Jackson2JsonDecoder(new ObjectMapper(), TEXT_HTML));
    }
}