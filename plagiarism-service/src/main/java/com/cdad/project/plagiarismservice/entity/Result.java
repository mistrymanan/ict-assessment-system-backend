package com.cdad.project.plagiarismservice.entity;

import com.cdad.project.plagiarismservice.ServiceClients.Language;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class Result {
    private String link;
    private Language language;
    private GraphData graphData;
}