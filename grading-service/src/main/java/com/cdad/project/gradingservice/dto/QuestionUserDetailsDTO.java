package com.cdad.project.gradingservice.dto;

import com.cdad.project.gradingservice.entity.QuestionStatus;
import com.cdad.project.gradingservice.entity.ResultStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuestionUserDetailsDTO {
    private String questionId;
    private String buildId;
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime time;
    private QuestionStatus questionStatus;
    private ResultStatus resultStatus;
    private Double totalPoints;
    private Double score;
}
