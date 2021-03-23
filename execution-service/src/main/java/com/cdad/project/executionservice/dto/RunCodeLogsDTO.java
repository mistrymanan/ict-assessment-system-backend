package com.cdad.project.executionservice.dto;

import com.cdad.project.executionservice.entity.Language;
import com.cdad.project.executionservice.entity.Status;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class RunCodeLogsDTO {
    private String id;
    private String name;
    private String email;
    private String picture;
    private Status status;
    private long executionTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime timeStamp;
    private Language language;
}
