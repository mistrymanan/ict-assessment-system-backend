package com.cdad.project.plagiarismservice.ServiceClients.mossapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetProcessedData {
    List<Link> links;
    List<Node> nodes;
}