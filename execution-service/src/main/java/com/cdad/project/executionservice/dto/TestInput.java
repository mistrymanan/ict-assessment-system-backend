package com.cdad.project.executionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestInput {
    private String id;
    private String input;
    public TestInput(String input) {
        this.input = input;
    }
}