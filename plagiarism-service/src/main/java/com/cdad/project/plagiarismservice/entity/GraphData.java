package com.cdad.project.plagiarismservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GraphData {
    List<Link> links;
    List<Node> nodes;
}