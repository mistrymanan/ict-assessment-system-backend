package com.cdad.project.assignmentservice.exchanges;

import com.cdad.project.assignmentservice.dto.QuestionDTO;
import lombok.Data;

@Data
public class UpdateAssignmentQuestionRequest {
    private String assignmentId;
    private QuestionDTO question;
}
