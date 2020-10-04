package com.cdad.project.gradingservice.dto;

import com.cdad.project.gradingservice.entity.QuestionStatus;
import com.cdad.project.gradingservice.entity.ResultStatus;
import com.cdad.project.gradingservice.exchange.TestResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private String questionId;
    private String buildId;
    private String title;
    private LocalDateTime time;
    private QuestionStatus questionStatus;
    private ResultStatus resultStatus;
    private Double totalPoints;
    private Double score;
    private List<TestResult> testResults;
}