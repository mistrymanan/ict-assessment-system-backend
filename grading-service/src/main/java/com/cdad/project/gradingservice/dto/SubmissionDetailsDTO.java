package com.cdad.project.gradingservice.dto;

import com.cdad.project.gradingservice.entity.QuestionEntity;
import com.cdad.project.gradingservice.entity.ResultStatus;
import com.cdad.project.gradingservice.entity.SubmissionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class SubmissionDetailsDTO {
    private UUID id;
    private String email;
    private String assignmentId;
    private ResultStatus status;
    private SubmissionStatus submissionStatus;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime time;
    private Double currentScore;
    //private List<QuestionEntity> questionEntities;
    private LocalDateTime startOn;
    private LocalDateTime completedOn;
}
