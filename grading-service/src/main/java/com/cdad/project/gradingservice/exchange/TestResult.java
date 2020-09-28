package com.cdad.project.gradingservice.exchange;

import com.cdad.project.gradingservice.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestResult {
    private String id;
    private Status status;
}
