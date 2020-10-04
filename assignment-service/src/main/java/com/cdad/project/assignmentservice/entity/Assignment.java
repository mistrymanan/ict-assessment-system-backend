package com.cdad.project.assignmentservice.entity;

import lombok.Data;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "assignments")
@Data
@ToString
public class Assignment {
  @MongoId(FieldType.OBJECT_ID)
  private ObjectId id;
  private String title;
  private String status;
  @Indexed
  private String email;
  @Indexed(unique = true)
  private String slug;
  private boolean timed;
  private Integer duration;
  private boolean hasStartTime;
  private LocalDateTime startTime;
  private boolean hasDeadline;
  private LocalDateTime deadline;
  private List<Question> questions;
}
