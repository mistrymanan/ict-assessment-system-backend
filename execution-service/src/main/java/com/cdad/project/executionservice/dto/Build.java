package com.cdad.project.executionservice.dto;

import com.cdad.project.executionservice.entity.BuildEntity;
import com.cdad.project.executionservice.entity.Language;
import com.cdad.project.executionservice.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Build {
private String id;
private Status status;
private String output;
public Build(BuildEntity entity){
    this.setId(entity.getId());
    this.setOutput(entity.getOutput());
    this.setStatus(entity.getStatus());
}
}
