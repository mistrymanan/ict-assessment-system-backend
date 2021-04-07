package com.cdad.project.plagiarismservice.ServiceClients;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserQuestionResponseDTO {
    private String name;
    private String sourceCode;
    private Language language;
}