package com.cdad.project.executionservice.dto;

import com.cdad.project.executionservice.entity.Status;
import lombok.Data;

@Data
public class TestCase {
    String testCase;
    String input;
    String output;
    Status status;
    long executionTime;
}
