package com.cdad.project.gradingservice.serviceclient.executionservice.dto;

import com.cdad.project.gradingservice.entity.Language;
import lombok.Data;

@Data
public class BuildCode {
    private String sourceCode;
    private Language language;
}