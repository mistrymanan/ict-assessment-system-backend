package com.cdad.project.assignmentservice.serviceclient.gradingservice.dto;

import com.cdad.project.assignmentservice.serviceclient.gradingservice.enums.QuestionStatus;
import com.cdad.project.assignmentservice.serviceclient.gradingservice.enums.ResultStatus;
import com.cdad.project.assignmentservice.serviceclient.gradingservice.enums.SubmissionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private String questionId;
    private String buildId;
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime time;
    private QuestionStatus questionStatus;
    private ResultStatus resultStatus;
    private Double totalPoints;
    private Double score;
    private List<TestResult> testResults;
}
