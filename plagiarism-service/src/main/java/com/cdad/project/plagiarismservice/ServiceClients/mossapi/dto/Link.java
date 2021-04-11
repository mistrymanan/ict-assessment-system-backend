package com.cdad.project.plagiarismservice.ServiceClients.mossapi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Link {
    private String first;
    private String second;
    private Integer weight;
}