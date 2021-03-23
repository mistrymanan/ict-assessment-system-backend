package com.cdad.project.executionservice.entity;

import com.cdad.project.executionservice.dto.TestOutput;
import com.cdad.project.executionservice.exchange.PostBuildRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RunCodeLog {
    @MongoId
    private String id;
    private String name;
    private String email;
    private String picture;
    private Status status;
    private String sourceCode;
    private long executionTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime timeStamp;
    private Language language;
    private String error;

    public RunCodeLog() {

    }

    public RunCodeLog(Status status, String sourceCode
            , long executionTime
            , LocalDateTime timeStamp
            , Language language
            , String error) {
        this.status = status;
        this.sourceCode = sourceCode;
        this.executionTime = executionTime;
        this.timeStamp = timeStamp;
        this.language = language;
        this.error = error;
    }
}
