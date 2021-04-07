package com.cdad.project.plagiarismservice.entity;


import com.cdad.project.plagiarismservice.ServiceClients.Language;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.HashMap;

@Data
@NoArgsConstructor
@Document(collection = "plagiarisms")
public class Plagiarism {
    @MongoId(FieldType.OBJECT_ID)
    private String id;
    private String classroomSlug;
    private String assignmentId;
    private String questionId;
    private HashMap<Language,String> resultLinkMap;
    private LocalDateTime time;
    private Integer numberOfSubmissions;
}
