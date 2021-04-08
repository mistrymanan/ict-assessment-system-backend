package com.cdad.project.plagiarismservice.dto;

import com.cdad.project.plagiarismservice.ServiceClients.Language;
import com.cdad.project.plagiarismservice.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.oauth2.jwt.Jwt;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class PlagiarismMessageDTO  implements Serializable {
    private String id;
    private String classroomSlug;
    private String assignmentId;
    private String questionId;
    private String status;
    private Jwt jwtToken;
}