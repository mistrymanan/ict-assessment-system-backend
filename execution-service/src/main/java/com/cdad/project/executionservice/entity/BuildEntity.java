package com.cdad.project.executionservice.entity;

import lombok.*;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "builds")
@Data()
@ToString
public class BuildEntity {
    @MongoId
    private String id;
    private Status status;
    private String output;
}
