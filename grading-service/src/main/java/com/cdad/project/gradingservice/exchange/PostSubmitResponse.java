package com.cdad.project.gradingservice.exchange;

import com.cdad.project.gradingservice.entity.Status;
import com.cdad.project.gradingservice.entity.SubmissionStatus;
import lombok.Data;

import java.util.List;

@Data
public class PostSubmitResponse {
    private String id;
    private String assignmentId;
    private String questionId;
    private String buildId;
    private SubmissionStatus submissionStatus;
    private Status status;
    private Double score;
    private List<ResponseTestCase> testCases;

}
