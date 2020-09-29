package com.cdad.project.gradingservice.exception;

import com.cdad.project.gradingservice.entity.ResultStatus;
import com.cdad.project.gradingservice.entity.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubmissionCompilationError extends Exception {
    private String buildId;
    private String assignmentId;
    private String questionId;
    private final ResultStatus status = ResultStatus.NOT_ACCEPTED;
    public SubmissionCompilationError(String message) {
        super(message);
    }
}
