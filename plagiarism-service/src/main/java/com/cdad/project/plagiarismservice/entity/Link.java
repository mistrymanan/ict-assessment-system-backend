package com.cdad.project.plagiarismservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Link {
    private String first;
    private String second;
    private Integer weight;
}