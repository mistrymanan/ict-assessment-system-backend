package com.cdad.project.plagiarismservice.dto;

import com.cdad.project.plagiarismservice.ServiceClients.Language;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResultDTO {
    private String link;
    private Language language;
}
