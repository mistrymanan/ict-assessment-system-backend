package com.cdad.project.executionservice.dto;

import com.cdad.project.executionservice.entity.Language;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BuildCode {
    private String sourceCode;
    private Language language;
}