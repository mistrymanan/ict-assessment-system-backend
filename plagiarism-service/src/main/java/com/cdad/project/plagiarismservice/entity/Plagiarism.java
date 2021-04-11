package com.cdad.project.plagiarismservice.entity;


import com.cdad.project.plagiarismservice.ServiceClients.Language;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
@Document(collection = "plagiarisms")
public class Plagiarism {
    @MongoId(FieldType.OBJECT_ID)
    private String id;
    private String classroomSlug;
    private String assignmentId;
    private String questionId;
    private Status status;
    private List<Result> results;
    private LocalDateTime time;
    private Integer numberOfSubmissions;

    public Plagiarism(String classroomSlug, String assignmentId, String questionId, Status status, List<Result> results, LocalDateTime time) {
        this.classroomSlug = classroomSlug;
        this.assignmentId = assignmentId;
        this.questionId = questionId;
        this.status = status;
        this.results = results;
        this.time = time;
    }
}
