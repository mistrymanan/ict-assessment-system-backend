package com.cdad.project.gradingservice.dto;

import com.cdad.project.gradingservice.entity.Language;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserQuestionResponseDTO {
    private String name;
    private String sourceCode;
    private Language language;
}