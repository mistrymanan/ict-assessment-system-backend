package com.cdad.project.gradingservice.exchange;

import com.cdad.project.gradingservice.entity.QuestionStatus;
import com.cdad.project.gradingservice.entity.ResultStatus;
import com.cdad.project.gradingservice.entity.Status;
import com.cdad.project.gradingservice.entity.SubmissionStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PostSubmitResponse {
    private String submissionId;
    private String assignmentId;
    private String questionId;
    private String buildId;
    private SubmissionStatus submissionStatus;
    private QuestionStatus questionStatus;
    private ResultStatus status;
    private Double score;
    private List<TestResult> testResults;

}
