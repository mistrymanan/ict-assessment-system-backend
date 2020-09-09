package com.cdad.project.assignmentservice.entity;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "assignments")
@Data
@ToString
public class AssignmentEntity {
  @MongoId
  private String id;
  private String title;
}
