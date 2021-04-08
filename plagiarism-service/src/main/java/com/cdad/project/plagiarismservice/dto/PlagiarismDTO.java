package com.cdad.project.plagiarismservice.dto;

import com.cdad.project.plagiarismservice.ServiceClients.Language;
import com.cdad.project.plagiarismservice.entity.Status;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.HashMap;

@Data
@NoArgsConstructor
@ToString
public class PlagiarismDTO {
    private String id;
    private String classroomSlug;
    private String assignmentId;
    private String questionId;
    private Status status;
    private LocalDateTime time;
    private Integer numberOfSubmissions;
}
