package com.cdad.project.gradingservice.exception;

import com.cdad.project.gradingservice.entity.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LanguageNotAllowedException extends Exception {
    private Status status = Status.LANGUAGE_NOT_ALLOWED;

    public LanguageNotAllowedException(String message) {
        super(message);
    }
}
