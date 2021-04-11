package com.cdad.project.plagiarismservice.dto;

import com.cdad.project.plagiarismservice.entity.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class PlagiarismResultDTO {
    private String id;
    private String classroomSlug;
    private String assignmentId;
    private String questionId;
    private Status status;
    private LocalDateTime time;
    private List<ResultDTO> results;
    private Integer numberOfSubmissions;
}
