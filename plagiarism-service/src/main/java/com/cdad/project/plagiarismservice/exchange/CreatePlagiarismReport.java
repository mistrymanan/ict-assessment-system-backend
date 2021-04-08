package com.cdad.project.plagiarismservice.exchange;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreatePlagiarismReport {
    private String classroomSlug;
    private String assignmentId;
    private String questionId;
}
