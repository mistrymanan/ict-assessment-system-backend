package com.cdad.project.gradingservice.dto;

import com.cdad.project.gradingservice.entity.ResultStatus;
import com.cdad.project.gradingservice.entity.Status;
import com.cdad.project.gradingservice.entity.SubmissionStatus;
import com.cdad.project.gradingservice.exchange.TestResult;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SubmissionResult {
    private String buildId;
    private SubmissionStatus submissionStatus;
    private ResultStatus status;
    private Double score;
    private LocalDateTime time;
    private List<TestResult> testResults;
}
