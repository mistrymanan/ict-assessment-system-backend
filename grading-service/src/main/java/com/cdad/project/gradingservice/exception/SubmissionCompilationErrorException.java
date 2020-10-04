package com.cdad.project.gradingservice.exception;

import com.cdad.project.gradingservice.entity.QuestionStatus;
import com.cdad.project.gradingservice.entity.ResultStatus;
import com.cdad.project.gradingservice.entity.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class SubmissionCompilationErrorException extends Exception {
    private String submissionId;
    private String buildId;
    private String assignmentId;
    private String questionId;
    private QuestionStatus questionStatus=QuestionStatus.IN_PROGRESS;
    private ResultStatus resultStatus = ResultStatus.NOT_ACCEPTED;
    public SubmissionCompilationErrorException(String message) {
        super(message);
    }
}
