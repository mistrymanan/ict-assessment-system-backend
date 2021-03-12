package com.cdad.project.gradingservice.exchange;

import com.cdad.project.gradingservice.entity.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LanguageNotAllowedErrorResponse {
    private Status status = Status.LANGUAGE_NOT_ALLOWED;
    private String message;
}
