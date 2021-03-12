package com.cdad.project.assignmentservice.dto;

import com.cdad.project.assignmentservice.serviceclient.gradingservice.enums.SubmissionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class ActiveAssignmentDTO {
    private String id;
    private String title;
    private String slug;
    private String status;
    private Integer totalPoints;
    private SubmissionStatus currentStatus;
    private boolean hasDeadline;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime deadline;
}
