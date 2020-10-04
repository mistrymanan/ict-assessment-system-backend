package com.cdad.project.gradingservice.entity;

import com.cdad.project.gradingservice.exchange.TestResult;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class QuestionEntity {
    private String questionId;
    private String buildId;
    private LocalDateTime time;
    private QuestionStatus questionStatus;
    private ResultStatus resultStatus;
    private Double totalPoints;
    private Double score;
    private List<TestResult> testResults;
}